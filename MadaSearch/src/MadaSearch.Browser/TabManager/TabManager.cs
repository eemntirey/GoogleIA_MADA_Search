using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;

namespace MadaSearch.Browser.TabManager;

public class TabManager : ITabManager
{
    private readonly List<TabItemModel> _tabs = new();
    private TabItemModel? _activeTab;

    public IReadOnlyList<TabItemModel> Tabs => _tabs.AsReadOnly();
    public TabItemModel? ActiveTab => _activeTab;

    public event EventHandler<TabItemModel>? ActiveTabChanged;
    public event EventHandler<TabItemModel>? TabCreated;
    public event EventHandler<TabItemModel>? TabClosed;

    public TabItemModel CreateTab(string url = "madasearch://home", bool isPrivate = false, Guid? workspaceId = null)
    {
        var tab = new TabItemModel
        {
            Url = url,
            Title = url == "madasearch://home" ? "Accueil — MadaSearch" : url,
            IsPrivate = isPrivate,
            WorkspaceId = workspaceId ?? Guid.Empty,
            Security = isPrivate ? SecurityStatus.Internal : SecurityStatus.Internal
        };

        _tabs.Add(tab);
        TabCreated?.Invoke(this, tab);
        SelectTab(tab.Id);
        return tab;
    }

    public void CloseTab(Guid tabId)
    {
        var tab = _tabs.FirstOrDefault(t => t.Id == tabId);
        if (tab == null) return;

        int index = _tabs.IndexOf(tab);
        _tabs.Remove(tab);
        TabClosed?.Invoke(this, tab);

        if (_activeTab?.Id == tabId)
        {
            if (_tabs.Count > 0)
            {
                int newIndex = Math.Min(index, _tabs.Count - 1);
                SelectTab(_tabs[newIndex].Id);
            }
            else
            {
                // Conserver au moins un onglet actif
                CreateTab();
            }
        }
    }

    public void SelectTab(Guid tabId)
    {
        var tab = _tabs.FirstOrDefault(t => t.Id == tabId);
        if (tab != null && _activeTab?.Id != tabId)
        {
            _activeTab = tab;
            ActiveTabChanged?.Invoke(this, tab);
        }
    }

    public void DuplicateTab(Guid tabId)
    {
        var source = _tabs.FirstOrDefault(t => t.Id == tabId);
        if (source != null)
        {
            CreateTab(source.Url, source.IsPrivate, source.WorkspaceId);
        }
    }

    public void MoveTabToWorkspace(Guid tabId, Guid workspaceId)
    {
        var tab = _tabs.FirstOrDefault(t => t.Id == tabId);
        if (tab != null)
        {
            tab.WorkspaceId = workspaceId;
        }
    }

    public void GroupTabsByDomain()
    {
        foreach (var tab in _tabs)
        {
            if (Uri.TryCreate(tab.Url, UriKind.Absolute, out var uri))
            {
                tab.GroupName = uri.Host;
            }
        }
    }
}
