using System.Text.RegularExpressions;

namespace MadaSearch.Core.Navigation;

public static class UrlValidator
{
    private static readonly Regex DomainRegex = new(
        @"^([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}(:\d+)?(/.*)?$",
        RegexOptions.Compiled | RegexOptions.IgnoreCase);

    private static readonly Regex LocalhostRegex = new(
        @"^(localhost|\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})(:\d+)?(/.*)?$",
        RegexOptions.Compiled | RegexOptions.IgnoreCase);

    public static bool IsValidUrl(string? input, out string formattedUrl)
    {
        formattedUrl = string.Empty;
        if (string.IsNullOrWhiteSpace(input)) return false;

        string trimmed = input.Trim();

        if (trimmed.StartsWith("madasearch://", StringComparison.OrdinalIgnoreCase) ||
            trimmed.StartsWith("about:", StringComparison.OrdinalIgnoreCase) ||
            trimmed.StartsWith("file://", StringComparison.OrdinalIgnoreCase))
        {
            formattedUrl = trimmed;
            return true;
        }

        if (trimmed.StartsWith("http://", StringComparison.OrdinalIgnoreCase) ||
            trimmed.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
        {
            if (Uri.TryCreate(trimmed, UriKind.Absolute, out var validatedUri))
            {
                formattedUrl = validatedUri.AbsoluteUri;
                return true;
            }
            return false;
        }

        // Check if looks like domain or IP/localhost
        if (DomainRegex.IsMatch(trimmed) || LocalhostRegex.IsMatch(trimmed))
        {
            string candidate = "https://" + trimmed;
            if (Uri.TryCreate(candidate, UriKind.Absolute, out var uri))
            {
                formattedUrl = uri.AbsoluteUri;
                return true;
            }
        }

        return false;
    }

    public static string NormalizeOrSearch(string input, string searchEngine = "Google")
    {
        if (IsValidUrl(input, out var url))
        {
            return url;
        }

        return Search.SearchEngineHelper.BuildSearchUrl(searchEngine, input);
    }
}
