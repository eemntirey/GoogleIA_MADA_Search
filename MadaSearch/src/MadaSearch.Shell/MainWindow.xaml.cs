using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using MadaSearch.Browser.ContentBlocking;
using MadaSearch.Browser.DataSaver;
using MadaSearch.Browser.OfflinePageService;
using MadaSearch.Core.Interfaces;
using MadaSearch.Shell.ViewModels;
using Microsoft.Web.WebView2.Core;

namespace MadaSearch.Shell;

public partial class MainWindow : Window
{
    private readonly MainViewModel _viewModel;
    private readonly IContentBlockerService _contentBlocker;
    private readonly IDataSaverService _dataSaver;

    public MainWindow(MainViewModel viewModel, IContentBlockerService contentBlocker, IDataSaverService dataSaver)
    {
        InitializeComponent();
        _viewModel = viewModel;
        _contentBlocker = contentBlocker;
        _dataSaver = dataSaver;
        DataContext = _viewModel;

        Loaded += MainWindow_Loaded;
    }

    private async void MainWindow_Loaded(object sender, RoutedEventArgs e)
    {
        await InitializeWebViewAsync();
    }

    private async Task InitializeWebViewAsync()
    {
        try
        {
            await BrowserWebView.EnsureCoreWebView2Async();
            ConfigureWebViewSettings();
            AttachWebViewEvents();

            NavigateInternal("https://www.google.mg");
        }
        catch (Exception ex)
        {
            MessageBox.Show($"Erreur lors de l'initialisation de WebView2 : {ex.Message}", "MadaSearch", MessageBoxButton.OK, MessageBoxImage.Warning);
        }
    }

    private void ConfigureWebViewSettings()
    {
        var settings = BrowserWebView.CoreWebView2.Settings;
        settings.IsScriptEnabled = true;
        settings.AreDefaultScriptDialogsEnabled = true;
        settings.IsWebMessageEnabled = true;
        settings.AreDevToolsEnabled = false;
    }

    private void AttachWebViewEvents()
    {
        var core = BrowserWebView.CoreWebView2;

        // Filtrage des requêtes pour DataSaver et Bloqueur de publicités
        core.AddWebResourceRequestedFilter("*", CoreWebView2WebResourceContext.All);
        core.WebResourceRequested += (s, args) =>
        {
            string uri = args.Request.Uri;

            // Bloquer pubs et traqueurs
            if (_contentBlocker.ShouldBlockUrl(uri, out string category))
            {
                args.Response = core.Environment.CreateWebResourceResponse(null, 403, "Blocked", "");
                _dataSaver.RecordBlockedResource(category);
                Dispatcher.Invoke(() =>
                {
                    _viewModel.DataSaverInfo = $"{_dataSaver.Metrics.EstimatedBytesSaved / 1024} Ko économisés";
                });
                return;
            }

            // DataSaver : blocage des images volumineuses si activé
            if (_dataSaver.IsEnabled && _dataSaver.BlockImages && args.ResourceContext == CoreWebView2WebResourceContext.Image)
            {
                args.Response = core.Environment.CreateWebResourceResponse(null, 204, "No Content", "");
                _dataSaver.RecordBlockedResource("image");
            }
        };

        core.NavigationStarting += (s, args) =>
        {
            Dispatcher.Invoke(() =>
            {
                if (_viewModel.ActiveTab != null)
                {
                    _viewModel.ActiveTab.IsLoading = true;
                    _viewModel.ActiveTab.Url = args.Uri;
                    _viewModel.CurrentUrlInput = args.Uri;
                }
            });
        };

        core.NavigationCompleted += (s, args) =>
        {
            Dispatcher.Invoke(() =>
            {
                if (_viewModel.ActiveTab != null)
                {
                    _viewModel.ActiveTab.IsLoading = false;
                    _viewModel.ActiveTab.Title = core.DocumentTitle ?? args.IsSuccess.ToString();
                }

                if (!args.IsSuccess)
                {
                    string html = OfflinePageGenerator.GenerateHtml("network_error", core.Source);
                    BrowserWebView.NavigateToString(html);
                }
            });
        };
    }

    private void NavigateInternal(string url)
    {
        try
        {
            BrowserWebView.CoreWebView2?.Navigate(url);
        }
        catch
        {
            // fallback
        }
    }

    private void OnBackClick(object sender, RoutedEventArgs e)
    {
        if (BrowserWebView.CanGoBack) BrowserWebView.GoBack();
    }

    private void OnForwardClick(object sender, RoutedEventArgs e)
    {
        if (BrowserWebView.CanGoForward) BrowserWebView.GoForward();
    }

    private void OnRefreshClick(object sender, RoutedEventArgs e)
    {
        BrowserWebView.Reload();
    }

    private void OnHomeClick(object sender, RoutedEventArgs e)
    {
        NavigateInternal("https://www.google.mg");
    }

    private void OnNewTabClick(object sender, RoutedEventArgs e)
    {
        _viewModel.AddNewTab();
        NavigateInternal("https://www.google.mg");
    }

    private void OnCloseTabClick(object sender, RoutedEventArgs e)
    {
        if (sender is Button btn && btn.Tag is Guid tabId)
        {
            _viewModel.CloseTab(tabId);
        }
    }

    private void OnAddBookmarkClick(object sender, RoutedEventArgs e)
    {
        _ = _viewModel.AddCurrentToBookmarksAsync();
    }

    private void OnAssistantClick(object sender, RoutedEventArgs e)
    {
        _ = _viewModel.SummarizePageAsync();
    }

    private void OnUrlKeyDown(object sender, KeyEventArgs e)
    {
        if (e.Key == Key.Enter)
        {
            _viewModel.NavigateTo(UrlTextBox.Text);
            if (_viewModel.ActiveTab != null)
            {
                NavigateInternal(_viewModel.ActiveTab.Url);
            }
        }
    }
}
