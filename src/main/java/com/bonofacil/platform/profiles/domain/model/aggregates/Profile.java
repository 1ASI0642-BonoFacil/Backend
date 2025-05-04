package com.bonofacil.platform.profiles.domain.model.aggregates;

import jakarta.persistence.*;
import com.bonofacil.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.bonofacil.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.bonofacil.platform.profiles.domain.model.valueobjects.NombreContacto;
import com.bonofacil.platform.profiles.domain.model.valueobjects.Password;
import com.bonofacil.platform.profiles.domain.model.valueobjects.RazonSocial;
import com.bonofacil.platform.profiles.domain.model.valueobjects.Ruc;
import com.bonofacil.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "value", column = @Column(name = "ruc_value", length = 11))
  })
  private Ruc ruc;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "value", column = @Column(name = "razon_social_value", length = 255))
  })
  private RazonSocial razonSocial;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "address", column = @Column(name = "contacto_email"))
  })
  private EmailAddress email; // Se mantiene como 'email'

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "value", column = @Column(name = "password_value"))
  })
  private Password password;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "value", column = @Column(name = "nombre_contacto_value", length = 255))
  })
  private NombreContacto nombreContacto;

  public Profile(Ruc ruc, RazonSocial razonSocial, EmailAddress email, Password password, NombreContacto nombreContacto) {
    this.ruc = ruc;
    this.razonSocial = razonSocial;
    this.email = email;
    this.password = password;
    this.nombreContacto = nombreContacto;
  }

  public Profile(CreateProfileCommand command) {
    this.ruc = new Ruc(command.ruc());
    this.razonSocial = new RazonSocial(command.razonSocial());
    this.email = new EmailAddress(command.email()); // Se mantiene como 'email'
    this.password = new Password(command.password());
    this.nombreContacto = new NombreContacto(command.nombreContacto());
  }

  public Profile() {
  }

  public void updateRuc(String ruc) {
    this.ruc = new Ruc(ruc);
  }

  public void updateRazonSocial(String razonSocial) {
    this.razonSocial = new RazonSocial(razonSocial);
  }

  public void updateEmail(String email) { // Se mantiene como 'updateEmail'
    this.email = new EmailAddress(email);
  }

  public void updatePassword(String password) {
    this.password = new Password(password);
  }

  public void updateNombreContacto(String nombreContacto) {
    this.nombreContacto = new NombreContacto(nombreContacto);
  }

  public String getRucValue() {
    return ruc != null ? ruc.value() : null;
  }

  public String getRazonSocialValue() {
    return razonSocial != null ? razonSocial.value() : null;
  }

  public String getEmailAddress() {
    return email != null ? email.address() : null; // Se mantiene como 'getEmailAddress'
  }

  public String getPasswordValue() {
    return password != null ? password.value() : null;
  }

  public String getNombreContactoValue() {
    return nombreContacto != null ? nombreContacto.value() : null;
  }
}