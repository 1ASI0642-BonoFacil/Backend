package com.bonofacil.platform.profiles.interfaces.acl;

import com.bonofacil.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.bonofacil.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.bonofacil.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.bonofacil.platform.profiles.domain.services.ProfileCommandService;
import com.bonofacil.platform.profiles.domain.services.ProfileQueryService;
import org.springframework.stereotype.Service;

/**
 * Service Facade for the Profile context.
 *
 * <p>
 * It is used by the other contexts to interact with the Profile context.
 * It is implemented as part of an anti-corruption layer (ACL) to be consumed by other contexts.
 * </p>
 *
 */
@Service
public class ProfilesContextFacade {
  private final ProfileCommandService profileCommandService;
  private final ProfileQueryService profileQueryService;

  public ProfilesContextFacade(ProfileCommandService profileCommandService,
                               ProfileQueryService profileQueryService) {
    this.profileCommandService = profileCommandService;
    this.profileQueryService = profileQueryService;
  }

  /**
   * Creates a new Profile for a company registration.
   *
   * @param ruc           The RUC of the company.
   * @param razonSocial   The company's legal name.
   * @param email         The contact email address.
   * @param password      The password for the company profile.
   * @param nombreContacto The name of the contact person (optional).
   * @return The profile id of the newly created company profile.
   */
  public Long createProfile(String ruc, String razonSocial, String email, String password, String nombreContacto) {
    var createProfileCommand = new CreateProfileCommand(ruc, razonSocial, email, password, nombreContacto);
    var profile = profileCommandService.handle(createProfileCommand);
    if (profile.isEmpty())
      return 0L;
    return profile.get().getId();
  }

  /**
   * Fetches the profile id by email.
   *
   * @param email The email address to search for.
   * @return The profile id associated with the given email, or 0L if not found.
   */
  public Long fetchProfileIdByEmail(String email) {
    var getProfileByEmailQuery = new GetProfileByEmailQuery(new EmailAddress(email));
    var profile = profileQueryService.handle(getProfileByEmailQuery);
    if (profile.isEmpty())
      return 0L;
    return profile.get().getId();
  }
}