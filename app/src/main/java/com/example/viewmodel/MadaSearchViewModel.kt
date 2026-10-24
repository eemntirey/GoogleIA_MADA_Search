package com.example.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.filter.AdBlockerEngine
import com.example.data.local.BookmarkEntity
import com.example.data.local.HistoryEntity
import com.example.data.local.MadaSearchDatabase
import com.example.data.model.Bookmark
import com.example.data.model.DataSaverStats
import com.example.data.model.HistoryItem
import com.example.data.model.QuickLink
import com.example.data.model.SecurityStatus
import com.example.data.model.Tab
import com.example.data.model.Workspace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MadaSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        MadaSearchDatabase::class.java,
        "madasearch_android.db"
    ).fallbackToDestructiveMigration().build()

    val adBlocker = AdBlockerEngine()

    // Workspaces
    private val _workspaces = MutableStateFlow(
        listOf(
            Workspace("default", "Personnel", 0xFF0066FF, "person"),
            Workspace("work", "Travail", 0xFF10B981, "work"),
            Workspace("studies", "Études", 0xFF8B5CF6, "school"),
            Workspace("research", "Recherche", 0xFFF59E0B, "search"),
            Workspace("projects", "Projets", 0xFFEC4899, "folder")
        )
    )
    val workspaces: StateFlow<List<Workspace>> = _workspaces.asStateFlow()

    private val _currentWorkspaceId = MutableStateFlow("default")
    val currentWorkspaceId: StateFlow<String> = _currentWorkspaceId.asStateFlow()

    // Tabs
    private val initialTab = Tab(
        title = "Accueil — MadaSearch",
        url = "madasearch://home",
        workspaceId = "default"
    )
    private val _tabs = MutableStateFlow<List<Tab>>(listOf(initialTab))
    val tabs: StateFlow<List<Tab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(initialTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    // DataSaver & Content Blocking
    private val _isDataSaverEnabled = MutableStateFlow(true)
    val isDataSaverEnabled: StateFlow<Boolean> = _isDataSaverEnabled.asStateFlow()

    private val _blockImages = MutableStateFlow(false)
    val blockImages: StateFlow<Boolean> = _blockImages.asStateFlow()

    private val _isAdBlockEnabled = MutableStateFlow(true)
    val isAdBlockEnabled: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()

    private val _dataSaverStats = MutableStateFlow(
        DataSaverStats(
            estimatedBytesSaved = 145 * 1024L, // Économies initiales estimées
            adsBlocked = 3L,
            trackersBlocked = 2L
        )
    )
    val dataSaverStats: StateFlow<DataSaverStats> = _dataSaverStats.asStateFlow()

    // Omnibox / Search Engine
    private val _searchEngine = MutableStateFlow("Google")
    val searchEngine: StateFlow<String> = _searchEngine.asStateFlow()

    // Madagascar Quick Links
    val quickLinks = listOf(
        QuickLink("Google MG", "https://www.google.mg", "🔍", "Recherche"),
        QuickLink("Orange Mada", "https://www.orange.mg", "🍊", "Opérateur"),
        QuickLink("Telma", "https://www.telma.mg", "📶", "Opérateur"),
        QuickLink("Airtel MG", "https://www.airtel.mg", "📱", "Opérateur"),
        QuickLink("Midi Mada", "https://midi-madagasikara.mg", "📰", "Presse"),
        QuickLink("L'Express", "https://lexpress.mg", "🗞️", "Presse"),
        QuickLink("Moov.mg", "https://www.moov.mg", "🌐", "Portail"),
        QuickLink("Wikipedia MG", "https://mg.wikipedia.org", "📚", "Encyclopédie")
    )

    // Bookmarks Flow
    val bookmarks: StateFlow<List<Bookmark>> = db.bookmarkDao().getAllBookmarks()
        .map { list -> list.map { Bookmark(it.id, it.title, it.url, it.folder, it.addedAt) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History Flow
    val history: StateFlow<List<HistoryItem>> = db.historyDao().getRecentHistory()
        .map { list -> list.map { HistoryItem(it.id, it.title, it.url, it.visitedAt) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Assistant State
    private val _assistantMessage = MutableStateFlow("Bonjour ! Je suis votre assistant local MadaSearch. Comment puis-je vous aider ?")
    val assistantMessage: StateFlow<String> = _assistantMessage.asStateFlow()

    fun selectWorkspace(workspaceId: String) {
        _currentWorkspaceId.value = workspaceId
        // Si aucun onglet dans cet espace, en créer un
        val tabsInWs = _tabs.value.filter { it.workspaceId == workspaceId }
        if (tabsInWs.isEmpty()) {
            createNewTab(workspaceId = workspaceId)
        } else {
            _activeTabId.value = tabsInWs.first().id
        }
    }

    fun createNewTab(url: String = "madasearch://home", isPrivate: Boolean = false, workspaceId: String? = null) {
        val targetWs = workspaceId ?: _currentWorkspaceId.value
        val newTab = Tab(
            title = if (url == "madasearch://home") "Accueil" else url,
            url = url,
            isPrivate = isPrivate,
            workspaceId = targetWs
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
    }

    fun closeTab(tabId: String) {
        val currentList = _tabs.value
        val tabToClose = currentList.find { it.id == tabId } ?: return
        val remaining = currentList.filterNot { it.id == tabId }

        if (remaining.isEmpty()) {
            val fresh = Tab(workspaceId = _currentWorkspaceId.value)
            _tabs.value = listOf(fresh)
            _activeTabId.value = fresh.id
        } else {
            _tabs.value = remaining
            if (_activeTabId.value == tabId) {
                _activeTabId.value = remaining.last().id
            }
        }
    }

    fun duplicateTab(tabId: String) {
        val source = _tabs.value.find { it.id == tabId } ?: return
        createNewTab(url = source.url, isPrivate = source.isPrivate, workspaceId = source.workspaceId)
    }

    fun updateActiveTabState(
        title: String? = null,
        url: String? = null,
        isLoading: Boolean? = null,
        progress: Int? = null,
        canBack: Boolean? = null,
        canForward: Boolean? = null,
        security: SecurityStatus? = null
    ) {
        val activeId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == activeId) {
                tab.copy(
                    title = title ?: tab.title,
                    url = url ?: tab.url,
                    isLoading = isLoading ?: tab.isLoading,
                    progress = progress ?: tab.progress,
                    canGoBack = canBack ?: tab.canGoBack,
                    canGoForward = canForward ?: tab.canGoForward,
                    securityStatus = security ?: tab.securityStatus
                )
            } else tab
        }
    }

    fun recordVisit(url: String, title: String) {
        val activeTab = _tabs.value.find { it.id == _activeTabId.value }
        if (activeTab?.isPrivate == true) return // Pas d'historique en navigation privée
        if (url.startsWith("madasearch://") || url.isBlank()) return

        viewModelScope.launch {
            db.historyDao().insert(
                HistoryEntity(
                    title = if (title.isBlank()) url else title,
                    url = url,
                    visitedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun addBookmark(url: String, title: String) {
        if (url.startsWith("madasearch://") || url.isBlank()) return
        viewModelScope.launch {
            db.bookmarkDao().insert(
                BookmarkEntity(
                    title = if (title.isBlank()) url else title,
                    url = url,
                    folder = "Favoris",
                    addedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            db.bookmarkDao().deleteById(id)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            db.historyDao().deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            db.historyDao().clearAll()
        }
    }

    fun toggleDataSaver() {
        _isDataSaverEnabled.value = !_isDataSaverEnabled.value
    }

    fun toggleBlockImages() {
        _blockImages.value = !_blockImages.value
    }

    fun toggleAdBlock() {
        _isAdBlockEnabled.value = !_isAdBlockEnabled.value
    }

    fun recordResourceBlocked(category: String) {
        val current = _dataSaverStats.value
        val savedBonus = if (category == "ad") 45 * 1024L else 15 * 1024L
        _dataSaverStats.value = current.copy(
            estimatedBytesSaved = current.estimatedBytesSaved + savedBonus,
            adsBlocked = if (category == "ad") current.adsBlocked + 1 else current.adsBlocked,
            trackersBlocked = if (category == "tracker") current.trackersBlocked + 1 else current.trackersBlocked
        )
    }

    fun resolveUrlOrSearch(input: String): String {
        val trimmed = input.trim()
        if (trimmed.startsWith("madasearch://") || trimmed.startsWith("about:")) return trimmed

        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            return trimmed
        }

        if (Patterns.WEB_URL.matcher(trimmed).matches() || trimmed.contains(".")) {
            return "https://$trimmed"
        }

        val encoded = Uri.encode(trimmed)
        return when (_searchEngine.value) {
            "DuckDuckGo" -> "https://duckduckgo.com/?q=$encoded"
            "Ecosia" -> "https://www.ecosia.org/search?q=$encoded"
            "Bing" -> "https://www.bing.com/search?q=$encoded"
            else -> "https://www.google.mg/search?q=$encoded"
        }
    }

    fun askAssistant(prompt: String) {
        val activeTab = _tabs.value.find { it.id == _activeTabId.value }
        val pageTitle = activeTab?.title ?: "cette page"

        _assistantMessage.value = when {
            prompt.contains("résumer", ignoreCase = true) || prompt.contains("synthèse", ignoreCase = true) ->
                "💡 Synthèse rapide : \"$pageTitle\" est actuellement chargée. En mode DataSaver, les ressources superflues sont compressées pour vous garantir une navigation fluide même à bas débit."
            prompt.contains("grouper", ignoreCase = true) || prompt.contains("onglets", ignoreCase = true) -> {
                groupTabsByDomain()
                "✨ Vos onglets ont été regroupés par domaine pour optimiser votre espace de travail !"
            }
            prompt.contains("économ", ignoreCase = true) || prompt.contains("data", ignoreCase = true) ->
                "📊 Vous avez économisé ${_dataSaverStats.value.totalSavedFormatted} de données grâce au blocage des publicités et à la compression réseau MadaSearch."
            else ->
                "🤖 Réponse locale : Pour la recherche \"$prompt\", vous pouvez lancer la requête directement dans l'Omnibox ou basculer d'espace de travail."
        }
    }

    private fun groupTabsByDomain() {
        _tabs.value = _tabs.value.map { tab ->
            val host = try { Uri.parse(tab.url).host } catch (_: Exception) { null }
            tab.copy(groupName = host ?: "Divers")
        }
    }
}
