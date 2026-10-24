using MadaSearch.Browser.ContentBlocking;
using MadaSearch.Browser.DataSaver;
using TabManagerImpl = MadaSearch.Browser.TabManager.TabManager;
using Xunit;

namespace MadaSearch.Browser.Tests;

public class ContentBlockerTests
{
    [Theory]
    [InlineData("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js", true, "ad")]
    [InlineData("https://www.google-analytics.com/analytics.js", true, "tracker")]
    [InlineData("https://www.lemonde.fr/article.html", false, "")]
    public void ShouldBlockUrl_IdentifiesBlockedDomains(string url, bool shouldBlock, string expectedCategory)
    {
        var blocker = new RuleBasedContentBlocker();
        bool result = blocker.ShouldBlockUrl(url, out string category);

        Assert.Equal(shouldBlock, result);
        Assert.Equal(expectedCategory, category);
    }

    [Fact]
    public void Whitelist_BypassesBlocking()
    {
        var blocker = new RuleBasedContentBlocker();
        string trackerUrl = "https://connect.facebook.net/en_US/sdk.js";

        Assert.True(blocker.ShouldBlockUrl(trackerUrl, out _));

        blocker.AddWhitelistDomain("connect.facebook.net");
        Assert.False(blocker.ShouldBlockUrl(trackerUrl, out _));
    }
}

public class DataSaverTests
{
    [Fact]
    public void RecordBlockedResource_AccuratelyIncrementsSavings()
    {
        var service = new DataSaverService();
        service.RecordBlockedResource("ad");
        service.RecordBlockedResource("tracker");

        var metrics = service.Metrics;
        Assert.Equal(1, metrics.BlockedAdsCount);
        Assert.Equal(1, metrics.BlockedTrackersCount);
        Assert.True(metrics.EstimatedBytesSaved > 0);
    }
}

public class TabManagerTests
{
    [Fact]
    public void CreateTab_AddsTabAndSelectsIt()
    {
        var mgr = new TabManagerImpl();
        var tab = mgr.CreateTab("https://www.madagascar-tribune.com");

        Assert.Single(mgr.Tabs);
        Assert.Equal(tab.Id, mgr.ActiveTab?.Id);
    }

    [Fact]
    public void CloseTab_MaintainsActiveTab()
    {
        var mgr = new TabManagerImpl();
        var tab1 = mgr.CreateTab("https://site1.com");
        var tab2 = mgr.CreateTab("https://site2.com");

        mgr.CloseTab(tab2.Id);
        Assert.Single(mgr.Tabs);
        Assert.Equal(tab1.Id, mgr.ActiveTab?.Id);
    }
}
