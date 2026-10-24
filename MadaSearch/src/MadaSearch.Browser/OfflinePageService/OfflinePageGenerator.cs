namespace MadaSearch.Browser.OfflinePageService;

public static class OfflinePageGenerator
{
    public static string GenerateHtml(string errorType, string failedUrl, string details = "")
    {
        string title = errorType switch
        {
            "dns" => "Impossible de trouver le serveur (Erreur DNS)",
            "offline" => "Vous êtes hors ligne",
            "timeout" => "Le délai d'attente est dépassé",
            "cert" => "Erreur de certificat de sécurité",
            _ => "Problème de chargement de la page"
        };

        string advice = errorType switch
        {
            "offline" => "Vérifiez vos câbles réseau, votre modem, vos données mobiles ou votre connexion Wi-Fi.",
            "dns" => "Vérifiez que l'adresse Web est correctement saisie ou que votre DNS répond.",
            "timeout" => "Le serveur met trop de temps à répondre. Votre connexion est peut-être lente.",
            _ => "Veuillez réessayer ultérieurement."
        };

        return $@"<!DOCTYPE html>
<html lang=""fr"">
<head>
    <meta charset=""utf-8"" />
    <meta name=""viewport"" content=""width=device-width, initial-scale=1"" />
    <title>{title} — MadaSearch</title>
    <style>
        body {{
            margin: 0;
            padding: 40px 20px;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background-color: #0A1118;
            color: #E2E8F0;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 80vh;
        }}
        .card {{
            background: #111C28;
            border: 1px solid #1E2D3D;
            border-radius: 12px;
            padding: 32px;
            max-width: 540px;
            width: 100%;
            box-shadow: 0 10px 25px rgba(0,0,0,0.5);
        }}
        .icon {{
            font-size: 42px;
            margin-bottom: 16px;
            color: #00D4FF;
        }}
        h1 {{
            font-size: 20px;
            margin: 0 0 12px 0;
            color: #FFFFFF;
        }}
        p {{
            font-size: 14px;
            line-height: 1.6;
            color: #94A3B8;
            margin: 0 0 16px 0;
        }}
        .url-box {{
            background: #0A1118;
            padding: 10px 14px;
            border-radius: 6px;
            font-family: monospace;
            font-size: 12px;
            color: #38BDF8;
            word-break: break-all;
            margin-bottom: 24px;
            border: 1px solid #1E2D3D;
        }}
        .btn {{
            display: inline-block;
            background: #0066FF;
            color: #FFFFFF;
            padding: 10px 20px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            border: none;
            transition: background 0.2s;
        }}
        .btn:hover {{
            background: #0052CC;
        }}
    </style>
</head>
<body>
    <div class=""card"">
        <div class=""icon"">⚡</div>
        <h1>{title}</h1>
        <p>{advice}</p>
        <div class=""url-box"">{System.Net.WebUtility.HtmlEncode(failedUrl)}</div>
        <button class=""btn"" onclick=""location.reload()"">Réessayer</button>
    </div>
</body>
</html>";
    }
}
