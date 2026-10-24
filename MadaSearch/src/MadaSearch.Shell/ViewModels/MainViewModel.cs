using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;
using MadaSearch.Core.Navigation;

namespace MadaSearch.Shell.ViewModels;

public class MainViewModel : INotifyPropertyChanged
{
    private readonly ITabManager _tabManager;
    private readonly IDataSaverService _dataSaverService;
    private readonly IBookmarkRepository _bookmarkRepository;
    private readonly IHistoryRepository _historyRepository;
    private readonly IAssistantService _assistantService;
    private readonly ISessionService _sessionService;

    private string _currentUrlInput = string.Empty;
    private bool _isDataSaverActive = true;
    private string _dataSaverInfo = "0 Ko économisés";
    private bool _isAssistantOpen = false;
    private string _assistantMessage = string.Empty;

    public MainViewModel(
        ITabManager tabManager,
        IDataSaverService dataSaverService,
        IBookmarkRepository bookmarkRepository,
        IHistoryRepository historyRepository,
        IAssistantService assistantService,
        ISessionService sessionService)
    {
        _tabManager = tabManager;
        _dataSaverService = dataSaverService;
        _bookmarkRepository = bookmarkRepository;
        _historyRepository = historyRepository;
        _assistantService = assistantService;
        _sessionService = sessionService;

        Tabs = new ObservableCollection<TabItemModel>(_tabManager.Tabs);

        _tabManager.TabCreated += (s, tab) =>
        {
            Tabs.Add(tab);
            OnPropertyChanged(nameof(ActiveTab));
        };

        _tabManager.TabClosed += (s, tab) =>
        {
            Tabs.Remove(tab);
            OnPropertyChanged(nameof(ActiveTab));
        };

        _tabManager.ActiveTabChanged += (s, tab) =>
        {
            OnPropertyChanged(nameof(ActiveTab));
            CurrentUrlInput = tab?.Url ?? string.Empty;
        };

        // Onglet initial
        if (Tabs.Count == 0)
        {
            _tabManager.CreateTab("madasearch://home");
        }
    }

    public ObservableCollection<TabItemModel> Tabs { get; }
    public TabItemModel? ActiveTab => _tabManager.ActiveTab;

    public string CurrentUrlInput
    {
        get => _currentUrlInput;
        set => SetField(ref _currentUrlInput, value);
    }

    public bool IsDataSaverActive
    {
        get => _isDataSaverActive;
        set
        {
            if (SetField(ref _isDataSaverActive, value))
            {
                _dataSaverService.IsEnabled = value;
            }
        }
    }

    public string DataSaverInfo
    {
        get => _dataSaverInfo;
        set => SetField(ref _dataSaverInfo, value);
    }

    public bool IsAssistantOpen
    {
        get => _isAssistantOpen;
        set => SetField(ref _isAssistantOpen, value);
    }

    public string AssistantMessage
    {
        get => _assistantMessage;
        set => SetField(ref _assistantMessage, value);
    }

    public void NavigateTo(string input)
    {
        string targetUrl = UrlValidator.NormalizeOrSearch(input);
        if (ActiveTab != null)
        {
            ActiveTab.Url = targetUrl;
            ActiveTab.Title = targetUrl;
            CurrentUrlInput = targetUrl;
            OnPropertyChanged(nameof(ActiveTab));
        }
    }

    public void AddNewTab()
    {
        _tabManager.CreateTab("madasearch://home");
    }

    public void CloseTab(Guid tabId)
    {
        _tabManager.CloseTab(tabId);
    }

    public async Task AddCurrentToBookmarksAsync()
    {
        if (ActiveTab != null && !string.IsNullOrWhiteSpace(ActiveTab.Url))
        {
            await _bookmarkRepository.AddAsync(ActiveTab.Title, ActiveTab.Url);
        }
    }

    public async Task SummarizePageAsync()
    {
        IsAssistantOpen = true;
        AssistantMessage = "Analyse en cours...";
        string context = ActiveTab?.Title ?? "Page active";
        AssistantMessage = await _assistantService.SummarizeTextAsync(context);
    }

    public event PropertyChangedEventHandler? PropertyChanged;
    protected void OnPropertyChanged([CallerMemberName] string? propertyName = null) =>
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));

    protected bool SetField<T>(ref T field, T value, [CallerMemberName] string? propertyName = null)
    {
        if (EqualityComparer<T>.Default.Equals(field, value)) return false;
        field = value;
        OnPropertyChanged(propertyName);
        return true;
    }
}
