using System.IO;
using System.Windows;
using MadaSearch.Assistant;
using MadaSearch.Browser.ContentBlocking;
using MadaSearch.Browser.DataSaver;
using MadaSearch.Browser.TabManager;
using MadaSearch.Core.Interfaces;
using MadaSearch.Data.Database;
using MadaSearch.Data.Migrations;
using MadaSearch.Data.Repositories;
using MadaSearch.Shell.ViewModels;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace MadaSearch.Shell;

public partial class App : Application
{
    public static IServiceProvider ServiceProvider { get; private set; } = null!;

    protected override async void OnStartup(StartupEventArgs e)
    {
        base.OnStartup(e);

        var services = new ServiceCollection();
        ConfigureServices(services);

        ServiceProvider = services.BuildServiceProvider();

        // Appliquer les migrations SQLite au démarrage
        var migrator = ServiceProvider.GetRequiredService<MigrationRunner>();
        await migrator.ApplyMigrationsAsync();

        var mainWindow = ServiceProvider.GetRequiredService<MainWindow>();
        mainWindow.Show();
    }

    private void ConfigureServices(IServiceCollection services)
    {
        services.AddLogging(builder =>
        {
            builder.AddDebug();
            builder.SetMinimumLevel(LogLevel.Information);
        });

        // Chemins de persistance locaux sécurisés (AppData/Local/MadaSearch)
        string appDataDir = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
            "MadaSearch");
        Directory.CreateDirectory(appDataDir);
        string dbPath = Path.Combine(appDataDir, "madasearch.db");

        services.AddSingleton(new SqliteConnectionFactory(dbPath));
        services.AddSingleton<MigrationRunner>();

        // Repositories
        services.AddSingleton<IBookmarkRepository, BookmarkRepository>();
        services.AddSingleton<IHistoryRepository, HistoryRepository>();
        services.AddSingleton<ISessionService, SessionRepository>();

        // Services du navigateur
        services.AddSingleton<ITabManager, TabManager>();
        services.AddSingleton<IDataSaverService, DataSaverService>();
        services.AddSingleton<IContentBlockerService, RuleBasedContentBlocker>();
        services.AddSingleton<IAssistantService, LocalTabOrganizerAssistant>();

        // ViewModels & Views
        services.AddTransient<MainViewModel>();
        services.AddTransient<MainWindow>();
    }
}
