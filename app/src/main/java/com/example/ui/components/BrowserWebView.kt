package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.filter.AdBlockerEngine
import com.example.data.model.SecurityStatus
import java.io.ByteArrayInputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    url: String,
    isDataSaverEnabled: Boolean,
    blockImages: Boolean,
    isAdBlockEnabled: Boolean,
    isPrivate: Boolean,
    adBlocker: AdBlockerEngine,
    onProgressChange: (Int) -> Unit,
    onTitleChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    onSecurityChange: (SecurityStatus) -> Unit,
    onNavigationStateChange: (canBack: Boolean, canForward: Boolean) -> Unit,
    onResourceBlocked: (String) -> Unit,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                // En mode DataSaver, optimiser le cache et limiter les images si souhaité
                cacheMode = if (isDataSaverEnabled) WebSettings.LOAD_CACHE_ELSE_NETWORK else WebSettings.LOAD_DEFAULT
                loadsImagesAutomatically = !blockImages
            }

            // Mode privé : ne pas sauvegarder l'historique web interne
            if (isPrivate) {
                clearHistory()
                clearCache(true)
            }
        }
    }

    DisposableEffect(webView) {
        onWebViewCreated(webView)
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = {
            webView.apply {
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        onProgressChange(newProgress)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        if (!title.isNullOrBlank()) {
                            onTitleChange(title)
                        }
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, currentUrl: String?, favicon: Bitmap?) {
                        onLoadingChange(true)
                        if (currentUrl != null) {
                            onUrlChange(currentUrl)
                            val isHttps = currentUrl.startsWith("https://", ignoreCase = true)
                            onSecurityChange(if (isHttps) SecurityStatus.SECURE_HTTPS else SecurityStatus.INSECURE_HTTP)
                        }
                        onNavigationStateChange(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                    }

                    override fun onPageFinished(view: WebView?, currentUrl: String?) {
                        onLoadingChange(false)
                        if (currentUrl != null) {
                            onUrlChange(currentUrl)
                        }
                        onNavigationStateChange(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val reqUrl = request?.url?.toString() ?: return null

                        if (isAdBlockEnabled) {
                            val (isBlocked, category) = adBlocker.isBlocked(reqUrl)
                            if (isBlocked) {
                                onResourceBlocked(category)
                                return WebResourceResponse(
                                    "text/plain",
                                    "utf-8",
                                    ByteArrayInputStream("".toByteArray())
                                )
                            }
                        }

                        if (isDataSaverEnabled && blockImages) {
                            val lower = reqUrl.lowercase()
                            if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".webp") || lower.endsWith(".gif")) {
                                onResourceBlocked("image")
                                return WebResourceResponse(
                                    "image/png",
                                    "utf-8",
                                    ByteArrayInputStream(ByteArray(0))
                                )
                            }
                        }

                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        onSecurityChange(SecurityStatus.WARNING)
                        // Par sécurité stricte, ne jamais contourner automatiquement les erreurs SSL
                        handler?.cancel()
                    }
                }
            }
        },
        update = { wv ->
            // Mettre à jour les réglages de chargement
            wv.settings.loadsImagesAutomatically = !blockImages
            wv.settings.cacheMode = if (isDataSaverEnabled) WebSettings.LOAD_CACHE_ELSE_NETWORK else WebSettings.LOAD_DEFAULT

            if (wv.url != url && !url.startsWith("madasearch://")) {
                wv.loadUrl(url)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
