package com.migration.infrastructure;

import com.migration.domain.persona.aggregation.PersonaDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonaDocumentRepository extends JpaRepository<PersonaDocument, Integer> {
}
