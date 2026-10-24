using MadaSearch.Core.Interfaces;

namespace MadaSearch.Browser.ContentBlocking;

public class RuleBasedContentBlocker : IContentBlockerService
{
    private readonly HashSet<string> _adDomains = new(StringComparer.OrdinalIgnoreCase)
    {
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "adservice.google.com",
        "criteo.com",
        "taboola.com",
        "outbrain.com",
        "adnxs.com",
        "popads.net",
        "adroll.com",
        "scorecardresearch.com",
        "rubiconproject.com",
        "amazon-adsystem.com"
    };

    private readonly HashSet<string> _trackerDomains = new(StringComparer.OrdinalIgnoreCase)
    {
        "google-analytics.com",
        "analytics.google.com",
        "hotjar.com",
        "connect.facebook.net",
        "facebook.com/tr",
        "clarity.ms",
        "segment.io",
        "mixpanel.com",
        "newrelic.com",
        "datadoghq.com"
    };

    private readonly HashSet<string> _whitelist = new(StringComparer.OrdinalIgnoreCase);

    public bool IsEnabled { get; set; } = true;

    public bool ShouldBlockUrl(string url, out string category)
    {
        category = string.Empty;
        if (!IsEnabled || string.IsNullOrWhiteSpace(url)) return false;

        if (!Uri.TryCreate(url, UriKind.Absolute, out var uri))
            return false;

        string host = uri.Host;

        // Check whitelist
        if (IsWhitelisted(host)) return false;

        // Check Ads
        foreach (var adDomain in _adDomains)
        {
            if (host.Equals(adDomain, StringComparison.OrdinalIgnoreCase) ||
                host.EndsWith("." + adDomain, StringComparison.OrdinalIgnoreCase))
            {
                category = "ad";
                return true;
            }
        }

        // Check Trackers
        foreach (var trackerDomain in _trackerDomains)
        {
            if (host.Equals(trackerDomain, StringComparison.OrdinalIgnoreCase) ||
                host.EndsWith("." + trackerDomain, StringComparison.OrdinalIgnoreCase))
            {
                category = "tracker";
                return true;
            }
        }

        return false;
    }

    public void AddWhitelistDomain(string domain)
    {
        if (!string.IsNullOrWhiteSpace(domain))
        {
            _whitelist.Add(domain.Trim().ToLowerInvariant());
        }
    }

    public void RemoveWhitelistDomain(string domain)
    {
        if (!string.IsNullOrWhiteSpace(domain))
        {
            _whitelist.Remove(domain.Trim().ToLowerInvariant());
        }
    }

    public bool IsWhitelisted(string domain)
    {
        if (string.IsNullOrWhiteSpace(domain)) return false;
        string clean = domain.Trim().ToLowerInvariant();
        return _whitelist.Contains(clean) || _whitelist.Any(w => clean.EndsWith("." + w));
    }
}
