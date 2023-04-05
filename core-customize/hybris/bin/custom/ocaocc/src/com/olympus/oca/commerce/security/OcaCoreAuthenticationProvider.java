package com.olympus.oca.commerce.security;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.LoginToken;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;
import de.hybris.platform.spring.security.CoreUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class OcaCoreAuthenticationProvider extends CoreAuthenticationProvider {

    private final UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (Registry.hasCurrentTenant() && JaloConnection.getInstance().isSystemInitialized()) {
            String username = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
            UserDetails userDetails = null;

            try {
                userDetails = this.retrieveUser(username);
            } catch (UsernameNotFoundException var6) {
                throw new BadCredentialsException(this.messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"), var6);
            }

            this.getPreAuthenticationChecks().check(userDetails);
            User user = UserManager.getInstance().getUserByLogin(userDetails.getUsername());
            this.additionalAuthenticationChecks(userDetails, (AbstractAuthenticationToken)authentication);
            this.postAuthenticationChecks.check(userDetails);
            JaloSession.getCurrentSession().setUser(user);
            return this.createSuccessAuthentication(authentication, userDetails);
        } else {
            return this.createSuccessAuthentication(authentication, new CoreUserDetails("systemNotInitialized", "systemNotInitialized", true, false, true, true, Collections.EMPTY_LIST, (String)null));
        }
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        private DefaultPostAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException(OcaCoreAuthenticationProvider.this.messages.getMessage("CoreAuthenticationProvider.credentialsExpired", "User credentials have expired"));
            }
        }
    }

}
