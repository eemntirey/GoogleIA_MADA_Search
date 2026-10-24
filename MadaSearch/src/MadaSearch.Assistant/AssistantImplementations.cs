using MadaSearch.Core.Interfaces;
using MadaSearch.Core.Models;

namespace MadaSearch.Assistant;

public class NoOpAssistantService : IAssistantService
{
    public string ProviderName => "NoOp (Aucune IA externe)";
    public bool IsAvailable => false;

    public Task<string> SummarizeTextAsync(string text, CancellationToken ct = default)
    {
        return Task.FromResult("L'assistant IA externe est désactivé. Aucune donnée n'a été transmise à un service externe.");
    }

    public Task<string> AnswerQuestionAsync(string pageContext, string question, CancellationToken ct = default)
    {
        return Task.FromResult("Service d'assistance IA non configuré ou désactivé pour respecter votre vie privée.");
    }

    public Task<IReadOnlyList<string>> SuggestTabGroupsAsync(IEnumerable<TabItemModel> tabs, CancellationToken ct = default)
    {
        IReadOnlyList<string> empty = Array.Empty<string>();
        return Task.FromResult(empty);
    }
}

public class LocalTabOrganizerAssistant : IAssistantService
{
    public string ProviderName => "Organisateur Local (Sans Cloud)";
    public bool IsAvailable => true;

    public Task<string> SummarizeTextAsync(string text, CancellationToken ct = default)
    {
        if (string.IsNullOrWhiteSpace(text)) return Task.FromResult("Aucun texte à résumer.");
        var sentences = text.Split(new[] { '.', '!', '?' }, StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries);
        var preview = string.Join(". ", sentences.Take(3)) + ".";
        return Task.FromResult($"[Résumé local 100% hors-ligne] : {preview}");
    }

    public Task<string> AnswerQuestionAsync(string pageContext, string question, CancellationToken ct = default)
    {
        return Task.FromResult("La recherche locale par mots-clés est active. Configurez une clé pour activer l'analyse sémantique avancée.");
    }

    public Task<IReadOnlyList<string>> SuggestTabGroupsAsync(IEnumerable<TabItemModel> tabs, CancellationToken ct = default)
    {
        var groups = tabs
            .Select(t => Uri.TryCreate(t.Url, UriKind.Absolute, out var u) ? u.Host : "Autres")
            .Distinct()
            .ToList();
        return Task.FromResult<IReadOnlyList<string>>(groups);
    }
}
