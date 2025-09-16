package sqlgenlib.config;

import java.util.List;

import sqlgenlib.core.model.Domain;

public record NormalizerConfig(
        List<Domain> clickDomains,
        List<Domain> gpDomains
) {}
