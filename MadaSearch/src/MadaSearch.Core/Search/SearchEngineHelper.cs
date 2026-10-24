namespace MadaSearch.Core.Search;

public static class SearchEngineHelper
{
    private static readonly Dictionary<string, string> SearchTemplates = new(StringComparer.OrdinalIgnoreCase)
    {
        { "Google", "https://www.google.com/search?q={0}" },
        { "DuckDuckGo", "https://duckduckgo.com/?q={0}" },
        { "Ecosia", "https://www.ecosia.org/search?q={0}" },
        { "Bing", "https://www.bing.com/search?q={0}" },
        { "Qwant", "https://www.qwant.com/?q={0}" }
    };

    public static IReadOnlyList<string> AvailableEngines => SearchTemplates.Keys.ToList();

    public static string BuildSearchUrl(string engineName, string query)
    {
        string encodedQuery = Uri.EscapeDataString(query.Trim());
        if (SearchTemplates.TryGetValue(engineName, out var template))
        {
            return string.Format(template, encodedQuery);
        }

        return string.Format(SearchTemplates["Google"], encodedQuery);
    }
}
