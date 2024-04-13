export const ROLES = {
  USER: 'USER',
  ARTIST: 'ARTIST',
  ADMIN: 'ADMIN'
};

export const hasPermission = (roles, permission) => roles?.includes(`ROLE_${permission}`);

export const hasAnyPermissions = (roles) => roles?.some((role) => role.startsWith('ROLE_'));

export const hasPermissions = (roles, permissions) =>
  permissions.some((permission) => roles?.includes(`ROLE_${permission}`));

export const hasSameUserIdOrPermissions = (ownerId, jwt, userPermissions, adminPermissions) =>
  (ownerId === jwt.id &&
    userPermissions.some((permission) => jwt.roles?.includes(`ROLE_${permission}`))) ||
  adminPermissions.some((permission) => jwt.roles?.includes(`ROLE_${permission}`));
