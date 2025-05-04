package com.bonofacil.platform.profiles.interfaces.rest;

import com.bonofacil.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import com.bonofacil.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.bonofacil.platform.profiles.domain.model.aggregates.Profile; // Import Profile
import com.bonofacil.platform.profiles.domain.services.ProfileCommandService;
import com.bonofacil.platform.profiles.domain.services.ProfileQueryService;
import com.bonofacil.platform.profiles.domain.services.ProfileService; // Import ProfileService
import com.bonofacil.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import com.bonofacil.platform.profiles.interfaces.rest.resources.ProfileResource;
import com.bonofacil.platform.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.bonofacil.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfilesController
 * <p>
 * This class is the entry point for all the REST endpoints related to the Profile entity.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Profile Management Endpoints")
public class ProfilesController {
  private final ProfileQueryService profileQueryService;
  private final ProfileCommandService profileCommandService;
  private final ProfileService profileService; // Inject ProfileService

  public ProfilesController(ProfileQueryService profileQueryService,
                            ProfileCommandService profileCommandService,
                            ProfileService profileService) { // Add ProfileService to constructor
    this.profileQueryService = profileQueryService;
    this.profileCommandService = profileCommandService;
    this.profileService = profileService;
  }

  /**
   * Creates a new Profile
   *
   * @param resource the resource containing the data to create the Profile
   * @return the created Profile
   */
  @PostMapping
  public ResponseEntity<ProfileResource> createProfile(
          @RequestBody CreateProfileResource resource) {
    var createProfileCommand = CreateProfileCommandFromResourceAssembler
            .toCommandFromResource(resource);
    var profile = profileCommandService.handle(createProfileCommand);
    if (profile.isEmpty())
      return ResponseEntity.badRequest().build();
    var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
    return new ResponseEntity<>(profileResource, HttpStatus.CREATED);
  }

  /**
   * Gets a Profile by its id
   *
   * @param profileId the id of the Profile to get
   * @return the Profile resource associated to given Profile id
   */
  @GetMapping("/{profileId}")
  public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long profileId) {
    var getProfileByIdQuery = new GetProfileByIdQuery(profileId);
    var profile = profileQueryService.handle(getProfileByIdQuery);
    if (profile.isEmpty())
      return ResponseEntity.badRequest().build();
    var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
    return ResponseEntity.ok(profileResource);
  }

  /**
   * Gets all the Profiles
   *
   * @return a list of all the Profile resources currently stored
   */
  @GetMapping
  public ResponseEntity<List<ProfileResource>> getAllProfiles() {
    var getAllProfilesQuery = new GetAllProfilesQuery();
    var profiles = profileQueryService.handle(getAllProfilesQuery);
    var profileResources = profiles.stream()
            .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
            .collect(Collectors.toList());
    return ResponseEntity.ok(profileResources);
  }

  /**
   * Assigns a Profile to a User
   * @param userId The ID of the user to assign the profile to
   * @param resource The data to create the profile.
   * @return
   */
  @PostMapping("/assign/{userId}")
  public ResponseEntity<ProfileResource> assignProfileToUser(
          @PathVariable Long userId,
          @RequestBody CreateProfileResource resource) {
    Profile createdProfile = profileService.createAndAssignProfile(userId, resource.ruc(), resource.razonSocial(), resource.email(), resource.password(), resource.nombreContacto());
    ProfileResource profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(createdProfile);
    return new ResponseEntity<>(profileResource, HttpStatus.CREATED);
  }
}
