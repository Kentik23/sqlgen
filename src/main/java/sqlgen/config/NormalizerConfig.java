package sqlgen.config;

import java.util.List;

import sqlgen.core.model.Domain;

public record NormalizerConfig(
        List<Domain> clickDomains,
        List<Domain> gpDomains
) {}
