package br.com.meetime.hubspotintegrator.dto.response;

public record ContactResponsePropertiesDto(
        String company,
        String createdate,
        String email,
        String firstname,
        String hs_all_contact_vids,
        String hs_associated_target_accounts,
        String hs_currently_enrolled_in_prospecting_agent,
        String hs_email_domain,
        String hs_full_name_or_email,
        String hs_is_contact,
        String hs_is_unworked,
        String hs_lifecyclestage_marketingqualifiedlead_date,
        String hs_membership_has_accessed_private_content,
        String hs_object_id,
        String hs_object_source,
        String hs_object_source_id,
        String hs_object_source_label,
        String hs_pipeline,
        String hs_prospecting_agent_actively_enrolled_count,
        String hs_registered_member,
        String hs_searchable_calculated_phone_number,
        String hs_sequences_actively_enrolled_count,
        String lastmodifieddate,
        String lastname,
        String lifecyclestage,
        String num_notes,
        String phone,
        String website
) {
}
