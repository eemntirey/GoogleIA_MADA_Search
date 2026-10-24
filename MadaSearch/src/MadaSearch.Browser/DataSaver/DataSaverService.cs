using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;
using Microsoft.Extensions.Logging;

namespace MadaSearch.Browser.DataSaver;

public class DataSaverService : IDataSaverService
{
    private readonly ILogger<DataSaverService>? _logger;
    private readonly DataSaverMetrics _metrics = new();
    private readonly object _lock = new();

    public DataSaverService(ILogger<DataSaverService>? logger = null)
    {
        _logger = logger;
    }

    public bool IsEnabled { get; set; } = true;
    public bool BlockImages { get; set; } = false;

    public DataSaverMetrics Metrics
    {
        get
        {
            lock (_lock)
            {
                return new DataSaverMetrics
                {
                    BytesTransferred = _metrics.BytesTransferred,
                    EstimatedBytesSaved = _metrics.EstimatedBytesSaved,
                    BlockedAdsCount = _metrics.BlockedAdsCount,
                    BlockedTrackersCount = _metrics.BlockedTrackersCount,
                    BlockedImagesCount = _metrics.BlockedImagesCount
                };
            }
        }
    }

    public void RecordTransferredBytes(long bytes)
    {
        if (bytes <= 0) return;
        lock (_lock)
        {
            _metrics.BytesTransferred += bytes;
        }
    }

    public void RecordSavedBytes(long estimatedBytes, string reason)
    {
        if (estimatedBytes <= 0) return;
        lock (_lock)
        {
            _metrics.EstimatedBytesSaved += estimatedBytes;
        }
        _logger?.LogDebug("DataSaver: {Bytes} octets économisés ({Reason})", estimatedBytes, reason);
    }

    public void RecordBlockedResource(string resourceType)
    {
        lock (_lock)
        {
            switch (resourceType.ToLowerInvariant())
            {
                case "ad":
                    _metrics.BlockedAdsCount++;
                    // Estimation réaliste basée sur la taille moyenne d'une bannière publicitaire (45 Ko)
                    _metrics.EstimatedBytesSaved += 45 * 1024;
                    break;
                case "tracker":
                    _metrics.BlockedTrackersCount++;
                    // Estimation script de tracking (15 Ko)
                    _metrics.EstimatedBytesSaved += 15 * 1024;
                    break;
                case "image":
                    _metrics.BlockedImagesCount++;
                    // Estimation image Web moyenne compressée (80 Ko)
                    _metrics.EstimatedBytesSaved += 80 * 1024;
                    break;
            }
        }
    }

    public void ResetMetrics()
    {
        lock (_lock)
        {
            _metrics.BytesTransferred = 0;
            _metrics.EstimatedBytesSaved = 0;
            _metrics.BlockedAdsCount = 0;
            _metrics.BlockedTrackersCount = 0;
            _metrics.BlockedImagesCount = 0;
        }
    }
}
