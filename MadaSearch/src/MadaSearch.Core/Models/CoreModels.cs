namespace MadaSearch.Core.Models;

public record TabItemModel
{
    public Guid Id { get; init; } = Guid.NewGuid();
    public string Title { get; set; } = "Nouvel onglet";
    public string Url { get; set; } = "madasearch://home";
    public bool IsLoading { get; set; }
    public double LoadingProgress { get; set; }
    public bool CanGoBack { get; set; }
    public bool CanGoForward { get; set; }
    public bool IsPrivate { get; init; }
    public Guid WorkspaceId { get; set; }
    public string? GroupName { get; set; }
    public SecurityStatus Security { get; set; } = SecurityStatus.Internal;
    public string? LastError { get; set; }
    public DateTime CreatedAt { get; init; } = DateTime.UtcNow;
}

public enum SecurityStatus
{
    Internal,
    SecureHttps,
    InsecureHttp,
    CertificateError,
    Blocked
}

public record WorkspaceModel
{
    public Guid Id { get; init; } = Guid.NewGuid();
    public string Name { get; set; } = "Général";
    public string ColorHex { get; set; } = "#0066FF";
    public string Icon { get; set; } = "Globe";
    public bool IsDefault { get; set; }
    public DateTime CreatedAt { get; init; } = DateTime.UtcNow;
}

public record BookmarkItem
{
    public long Id { get; init; }
    public string Title { get; set; } = string.Empty;
    public string Url { get; set; } = string.Empty;
    public string? Folder { get; set; } = "Favoris";
    public DateTime AddedAt { get; init; } = DateTime.UtcNow;
}

public record HistoryEntry
{
    public long Id { get; init; }
    public string Title { get; set; } = string.Empty;
    public string Url { get; set; } = string.Empty;
    public DateTime VisitedAt { get; init; } = DateTime.UtcNow;
    public int VisitCount { get; set; } = 1;
}

public record DownloadItem
{
    public Guid Id { get; init; } = Guid.NewGuid();
    public string FileName { get; set; } = string.Empty;
    public string Url { get; set; } = string.Empty;
    public string TargetFilePath { get; set; } = string.Empty;
    public long TotalBytes { get; set; }
    public long ReceivedBytes { get; set; }
    public double ProgressPercentage => TotalBytes > 0 ? (double)ReceivedBytes / TotalBytes * 100.0 : 0.0;
    public DownloadStatus Status { get; set; } = DownloadStatus.Pending;
    public string? ErrorMessage { get; set; }
    public DateTime StartedAt { get; init; } = DateTime.UtcNow;
}

public enum DownloadStatus
{
    Pending,
    Downloading,
    Paused,
    Completed,
    Cancelled,
    Failed
}

public record DataSaverMetrics
{
    public long BytesTransferred { get; set; }
    public long EstimatedBytesSaved { get; set; }
    public long BlockedAdsCount { get; set; }
    public long BlockedTrackersCount { get; set; }
    public long BlockedImagesCount { get; set; }

    public double SavingsPercentage =>
        (BytesTransferred + EstimatedBytesSaved) > 0
            ? (double)EstimatedBytesSaved / (BytesTransferred + EstimatedBytesSaved) * 100.0
            : 0.0;
}

public record BrowserSettings
{
    public string Theme { get; set; } = "Dark";
    public string SearchEngine { get; set; } = "Google";
    public string HomePageUrl { get; set; } = "madasearch://home";
    public bool EnableDataSaver { get; set; } = true;
    public bool BlockImagesOnDataSaver { get; set; } = false;
    public bool EnableAdBlocker { get; set; } = true;
    public bool RestoreSessionOnStartup { get; set; } = true;
    public string DefaultDownloadDirectory { get; set; } = string.Empty;
    public string Language { get; set; } = "fr-FR";
}

public record AssistantState
{
    public bool IsEnabled { get; set; } = false;
    public bool HasUserConsent { get; set; } = false;
    public string ActiveProvider { get; set; } = "LocalNoOp";
    public bool IsProcessing { get; set; } = false;
    public string LastResult { get; set; } = string.Empty;
}
