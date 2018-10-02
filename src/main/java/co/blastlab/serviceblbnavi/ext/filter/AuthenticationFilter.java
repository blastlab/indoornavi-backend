package co.blastlab.serviceblbnavi.ext.filter;

import co.blastlab.serviceblbnavi.dao.repository.ApiKeyRepository;
import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.ApiKey;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.rest.facade.auth.AuthService;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AuthorizedAccess
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	private UserRepository userRepository;

	@Inject
	private ApiKeyRepository apiKeyRepository;

	@Inject
	private AuthService authService;

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null || (!authorizationHeader.startsWith("Bearer ") && !authorizationHeader.startsWith("Token "))) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		if (authorizationHeader.startsWith("Bearer ")) {
			filterForBearer(authorizationHeader, requestContext);
		} else {
			filterForToken(authorizationHeader, requestContext);
		}
	}

	private void filterForBearer(String authorizationHeader, ContainerRequestContext requestContext) {
		String bearer = authorizationHeader.substring("Bearer".length()).trim();

		try {
			User user = validateBearer(bearer);

			Class<?> resourceClass = resourceInfo.getResourceClass();
			String classPermission = extractPermission(resourceClass);

			Method resourceMethod = resourceInfo.getResourceMethod();
			String methodPermission = extractPermission(resourceMethod);

			if (methodPermission == null) {
				checkPermissions(user, classPermission);
			} else if (!methodPermission.isEmpty()) {
				checkPermissions(user, methodPermission);
			}

			final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
			requestContext.setSecurityContext(new SecurityContext() {
				@Override
				public Principal getUserPrincipal() {
					return user;
				}

				@Override
				public boolean isUserInRole(String role) {
					return true;
				}

				@Override
				public boolean isSecure() {
					return currentSecurityContext.isSecure();
				}

				@Override
				public String getAuthenticationScheme() {
					return "Bearer";
				}
			});

		} catch (TokenInvalidOrExpired e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		} catch (UserNotAllowedToPerformAction e) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
	}

	private void filterForToken(String authorizationHeader, ContainerRequestContext requestContext) {
		String token = authorizationHeader.substring("Token".length()).trim();

		try {
			User user = validateToken(token);

			final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
			requestContext.setSecurityContext(new SecurityContext() {
				@Override
				public Principal getUserPrincipal() {
					return user;
				}

				@Override
				public boolean isUserInRole(String role) {
					return true;
				}

				@Override
				public boolean isSecure() {
					return currentSecurityContext.isSecure();
				}

				@Override
				public String getAuthenticationScheme() {
					return "Token";
				}
			});

		} catch (TokenInvalidOrExpired e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	private User validateBearer(String token) throws TokenInvalidOrExpired {
		User user = userRepository.findOptionalByToken(token).orElseThrow(TokenInvalidOrExpired::new);
		Date now = new Date();

		if (user.getTokenExpires() != null && now.after(user.getTokenExpires())) {
			throw new TokenInvalidOrExpired();
		}

		user.setTokenExpires(DateUtils.addMinutes(now, 30));

		return authService.save(user);
	}

	private User validateToken(String token) throws TokenInvalidOrExpired {
		ApiKey apiKey = apiKeyRepository.findOptionalByValue(token).orElseThrow(TokenInvalidOrExpired::new);
		return apiKey.getUser();
	}

	private void checkPermissions(User user, String permission) throws UserNotAllowedToPerformAction {
		Set<String> userPermissions = new HashSet<>();
		user.getPermissionGroups().forEach(
			permissionGroup -> userPermissions.addAll(permissionGroup.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()))
		);

		if (!userPermissions.contains(permission)) {
			throw new UserNotAllowedToPerformAction();
		}
	}

	private String extractPermission(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return null;
		} else {
			AuthorizedAccess autorizedAccess = annotatedElement.getAnnotation(AuthorizedAccess.class);
			if (autorizedAccess == null) {
				return null;
			} else {
				return autorizedAccess.value();
			}
		}
	}

	private class TokenInvalidOrExpired extends Throwable {}

	private class UserNotAllowedToPerformAction extends Throwable {}
}
