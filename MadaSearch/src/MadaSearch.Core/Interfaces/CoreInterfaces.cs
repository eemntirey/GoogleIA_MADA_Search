using MadaSearch.Core.Models;

namespace MadaSearch.Core.Interfaces;

public interface ITabManager
{
    IReadOnlyList<TabItemModel> Tabs { get; }
    TabItemModel? ActiveTab { get; }
    event EventHandler<TabItemModel>? ActiveTabChanged;
    event EventHandler<TabItemModel>? TabCreated;
    event EventHandler<TabItemModel>? TabClosed;

    TabItemModel CreateTab(string url = "madasearch://home", bool isPrivate = false, Guid? workspaceId = null);
    void CloseTab(Guid tabId);
    void SelectTab(Guid tabId);
    void DuplicateTab(Guid tabId);
    void MoveTabToWorkspace(Guid tabId, Guid workspaceId);
    void GroupTabsByDomain();
}

public interface IWorkspaceService
{
    IReadOnlyList<WorkspaceModel> Workspaces { get; }
    WorkspaceModel CurrentWorkspace { get; }
    event EventHandler<WorkspaceModel>? CurrentWorkspaceChanged;

    void SetCurrentWorkspace(Guid workspaceId);
    WorkspaceModel CreateWorkspace(string name, string colorHex, string icon);
    void DeleteWorkspace(Guid workspaceId);
}

public interface IDataSaverService
{
    bool IsEnabled { get; set; }
    bool BlockImages { get; set; }
    DataSaverMetrics Metrics { get; }
    void RecordTransferredBytes(long bytes);
    void RecordSavedBytes(long estimatedBytes, string reason);
    void RecordBlockedResource(string resourceType);
    void ResetMetrics();
}

public interface IContentBlockerService
{
    bool IsEnabled { get; set; }
    bool ShouldBlockUrl(string url, out string category);
    void AddWhitelistDomain(string domain);
    void RemoveWhitelistDomain(string domain);
    bool IsWhitelisted(string domain);
}

public interface IBookmarkRepository
{
    Task<IReadOnlyList<BookmarkItem>> GetAllAsync(CancellationToken ct = default);
    Task<BookmarkItem> AddAsync(string title, string url, string folder = "Favoris", CancellationToken ct = default);
    Task DeleteAsync(long id, CancellationToken ct = default);
    Task<bool> ExistsAsync(string url, CancellationToken ct = default);
}

public interface IHistoryRepository
{
    Task<IReadOnlyList<HistoryEntry>> GetRecentAsync(int limit = 100, CancellationToken ct = default);
    Task<IReadOnlyList<HistoryEntry>> SearchAsync(string query, CancellationToken ct = default);
    Task AddEntryAsync(string title, string url, CancellationToken ct = default);
    Task DeleteEntryAsync(long id, CancellationToken ct = default);
    Task ClearAllAsync(CancellationToken ct = default);
    Task ClearTimeRangeAsync(DateTime from, DateTime to, CancellationToken ct = default);
}

public interface ISessionService
{
    Task SaveSessionAsync(IEnumerable<TabItemModel> tabs, Guid activeWorkspaceId, CancellationToken ct = default);
    Task<(IReadOnlyList<TabItemModel> tabs, Guid activeWorkspaceId)?> RestoreSessionAsync(CancellationToken ct = default);
    Task ClearSessionAsync(CancellationToken ct = default);
}

public interface ISettingsService
{
    BrowserSettings Current { get; }
    Task SaveSettingsAsync(BrowserSettings settings, CancellationToken ct = default);
    Task<BrowserSettings> LoadSettingsAsync(CancellationToken ct = default);
}

public interface IAssistantService
{
    string ProviderName { get; }
    bool IsAvailable { get; }
    Task<string> SummarizeTextAsync(string text, CancellationToken ct = default);
    Task<string> AnswerQuestionAsync(string pageContext, string question, CancellationToken ct = default);
    Task<IReadOnlyList<string>> SuggestTabGroupsAsync(IEnumerable<TabItemModel> tabs, CancellationToken ct = default);
}
