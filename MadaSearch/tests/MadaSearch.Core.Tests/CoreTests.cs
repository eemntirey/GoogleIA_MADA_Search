using MadaSearch.Core.Navigation;
using MadaSearch.Core.Search;
using Xunit;

namespace MadaSearch.Core.Tests;

public class UrlValidatorTests
{
    [Theory]
    [InlineData("https://www.google.mg", true, "https://www.google.mg/")]
    [InlineData("http://madagascar-tribune.com", true, "http://madagascar-tribune.com/")]
    [InlineData("orange.mg", true, "https://orange.mg/")]
    [InlineData("madasearch://home", true, "madasearch://home")]
    public void IsValidUrl_ValidUrls_ReturnsTrue(string input, bool expected, string expectedUrl)
    {
        bool result = UrlValidator.IsValidUrl(input, out var formatted);
        Assert.Equal(expected, result);
        Assert.Equal(expectedUrl, formatted);
    }

    [Theory]
    [InlineData("recette romazava madagascar")]
    [InlineData("météo antananarivo")]
    [InlineData("")]
    public void IsValidUrl_SearchQueries_ReturnsFalse(string input)
    {
        bool result = UrlValidator.IsValidUrl(input, out _);
        Assert.False(result);
    }

    [Fact]
    public void NormalizeOrSearch_FormatsGoogleSearchCorrectly()
    {
        string query = "actualités madagascar";
        string result = UrlValidator.NormalizeOrSearch(query, "Google");
        Assert.Contains("google.com/search?q=", result);
        Assert.Contains("actualit%C3%A9s", result);
    }
}

public class SearchEngineHelperTests
{
    [Fact]
    public void BuildSearchUrl_DuckDuckGo_EncodesQuery()
    {
        string url = SearchEngineHelper.BuildSearchUrl("DuckDuckGo", "telma madagascar");
        Assert.Equal("https://duckduckgo.com/?q=telma%20madagascar", url);
    }

    [Fact]
    public void BuildSearchUrl_UnknownEngine_FallsBackToGoogle()
    {
        string url = SearchEngineHelper.BuildSearchUrl("UnknownEngine", "test");
        Assert.Contains("google.com/search?q=test", url);
    }
}
