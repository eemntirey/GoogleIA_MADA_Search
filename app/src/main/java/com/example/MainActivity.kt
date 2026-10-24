package com.example

import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.SecurityStatus
import com.example.ui.components.AssistantSheet
import com.example.ui.components.BookmarksHistorySheet
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.BrowserWebView
import com.example.ui.components.MadagascarHubView
import com.example.ui.components.OmniboxBar
import com.example.ui.components.TabsOverviewSheet
import com.example.ui.components.WorkspacesBar
import com.example.ui.theme.MadaCyan
import com.example.ui.theme.MadaDeepDark
import com.example.ui.theme.MadaElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MadaSearchViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MadaSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MadaSearchApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MadaSearchApp(viewModel: MadaSearchViewModel) {
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val workspaces by viewModel.workspaces.collectAsState()
    val currentWorkspaceId by viewModel.currentWorkspaceId.collectAsState()
    val isDataSaverEnabled by viewModel.isDataSaverEnabled.collectAsState()
    val blockImages by viewModel.blockImages.collectAsState()
    val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsState()
    val dataSaverStats by viewModel.dataSaverStats.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val assistantMessage by viewModel.assistantMessage.collectAsState()

    val activeTab = tabs.find { it.id == activeTabId } ?: tabs.firstOrNull()

    var activeWebView by remember { mutableStateOf<WebView?>(null) }

    // Bottom Sheet States
    var showTabsSheet by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showAssistantSheet by remember { mutableStateOf(false) }

    val tabsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bookmarksSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val assistantSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isCurrentBookmarked = bookmarks.any { it.url == activeTab?.url }

    // Gérer le retour physique / geste de retour
    BackHandler(enabled = true) {
        when {
            showTabsSheet -> showTabsSheet = false
            showBookmarksSheet -> showBookmarksSheet = false
            showAssistantSheet -> showAssistantSheet = false
            activeWebView?.canGoBack() == true -> activeWebView?.goBack()
            activeTab?.url != "madasearch://home" -> {
                viewModel.updateActiveTabState(
                    title = "Accueil — MadaSearch",
                    url = "madasearch://home"
                )
            }
            tabs.size > 1 -> {
                viewModel.closeTab(activeTab.id)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("madasearch_root"),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MadaDeepDark)
                    .statusBarsPadding()
            ) {
                // Workspace Pills
                WorkspacesBar(
                    workspaces = workspaces,
                    selectedWorkspaceId = currentWorkspaceId,
                    onWorkspaceSelected = { wsId ->
                        viewModel.selectWorkspace(wsId)
                    }
                )

                // Omnibox
                OmniboxBar(
                    currentUrl = activeTab?.url ?: "madasearch://home",
                    isLoading = activeTab?.isLoading ?: false,
                    securityStatus = activeTab?.securityStatus ?: SecurityStatus.INTERNAL,
                    tabCount = tabs.size,
                    isDataSaverActive = isDataSaverEnabled,
                    dataSaverSavings = dataSaverStats.totalSavedFormatted,
                    isBookmarked = isCurrentBookmarked,
                    onNavigate = { input ->
                        val target = viewModel.resolveUrlOrSearch(input)
                        viewModel.updateActiveTabState(
                            title = target,
                            url = target,
                            isLoading = true
                        )
                    },
                    onReload = {
                        activeWebView?.reload()
                    },
                    onTabsClick = { showTabsSheet = true },
                    onBookmarkClick = {
                        val current = activeTab?.url ?: ""
                        if (current.isNotBlank() && current != "madasearch://home") {
                            viewModel.addBookmark(current, activeTab?.title ?: current)
                        }
                    },
                    onDataSaverClick = {
                        viewModel.toggleDataSaver()
                    }
                )

                // Loading progress bar
                AnimatedVisibility(visible = activeTab?.isLoading == true) {
                    val progress = (activeTab?.progress ?: 0) / 100f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MadaCyan,
                        trackColor = Color.Transparent
                    )
                }
            }
        },
        bottomBar = {
            BrowserBottomBar(
                canGoBack = activeTab?.canGoBack ?: false,
                canGoForward = activeTab?.canGoForward ?: false,
                isDataSaverEnabled = isDataSaverEnabled,
                isAdBlockEnabled = isAdBlockEnabled,
                onBack = { activeWebView?.goBack() },
                onForward = { activeWebView?.goForward() },
                onHome = {
                    viewModel.updateActiveTabState(
                        title = "Accueil — MadaSearch",
                        url = "madasearch://home"
                    )
                },
                onOpenBookmarksHistory = { showBookmarksSheet = true },
                onOpenAssistant = { showAssistantSheet = true },
                onToggleDataSaver = { viewModel.toggleDataSaver() },
                onToggleAdBlock = { viewModel.toggleAdBlock() },
                onNewPrivateTab = {
                    viewModel.createNewTab(isPrivate = true)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MadaDeepDark)
        ) {
            val currentUrl = activeTab?.url ?: "madasearch://home"

            if (currentUrl == "madasearch://home") {
                MadagascarHubView(
                    quickLinks = viewModel.quickLinks,
                    dataSaverStats = dataSaverStats,
                    isDataSaverEnabled = isDataSaverEnabled,
                    onNavigate = { target ->
                        viewModel.updateActiveTabState(
                            title = target,
                            url = target,
                            isLoading = true
                        )
                    },
                    onAssistantClick = { showAssistantSheet = true },
                    onToggleDataSaver = { viewModel.toggleDataSaver() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                BrowserWebView(
                    url = currentUrl,
                    isDataSaverEnabled = isDataSaverEnabled,
                    blockImages = blockImages,
                    isAdBlockEnabled = isAdBlockEnabled,
                    isPrivate = activeTab?.isPrivate ?: false,
                    adBlocker = viewModel.adBlocker,
                    onProgressChange = { p ->
                        viewModel.updateActiveTabState(progress = p)
                    },
                    onTitleChange = { t ->
                        viewModel.updateActiveTabState(title = t)
                        if (activeTab?.url != null) {
                            viewModel.recordVisit(activeTab.url, t)
                        }
                    },
                    onUrlChange = { u ->
                        viewModel.updateActiveTabState(url = u)
                    },
                    onLoadingChange = { loading ->
                        viewModel.updateActiveTabState(isLoading = loading)
                    },
                    onSecurityChange = { sec ->
                        viewModel.updateActiveTabState(security = sec)
                    },
                    onNavigationStateChange = { canBack, canForward ->
                        viewModel.updateActiveTabState(canBack = canBack, canForward = canForward)
                    },
                    onResourceBlocked = { cat ->
                        viewModel.recordResourceBlocked(cat)
                    },
                    onWebViewCreated = { wv ->
                        activeWebView = wv
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Modal Sheets
    if (showTabsSheet) {
        TabsOverviewSheet(
            tabs = tabs,
            activeTabId = activeTabId,
            sheetState = tabsSheetState,
            onDismiss = { showTabsSheet = false },
            onSelectTab = { id -> viewModel.selectTab(id) },
            onCloseTab = { id -> viewModel.closeTab(id) },
            onDuplicateTab = { id -> viewModel.duplicateTab(id) },
            onNewTab = { isPrivate ->
                viewModel.createNewTab(isPrivate = isPrivate)
            }
        )
    }

    if (showBookmarksSheet) {
        BookmarksHistorySheet(
            bookmarks = bookmarks,
            history = history,
            sheetState = bookmarksSheetState,
            onDismiss = { showBookmarksSheet = false },
            onNavigate = { url ->
                viewModel.updateActiveTabState(
                    title = url,
                    url = url,
                    isLoading = true
                )
            },
            onDeleteBookmark = { id -> viewModel.deleteBookmark(id) },
            onDeleteHistoryItem = { id -> viewModel.deleteHistoryItem(id) },
            onClearAllHistory = { viewModel.clearAllHistory() }
        )
    }

    if (showAssistantSheet) {
        AssistantSheet(
            assistantMessage = assistantMessage,
            sheetState = assistantSheetState,
            onDismiss = { showAssistantSheet = false },
            onAsk = { query ->
                viewModel.askAssistant(query)
            }
        )
    }
}
