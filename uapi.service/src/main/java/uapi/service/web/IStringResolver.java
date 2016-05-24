package uapi.service.web;

import uapi.IIdentifiable;
import uapi.helper.IValueResolver;

/**
 * The String resolve
 */
public interface IStringResolver<T> extends IValueResolver<String, T>, IIdentifiable<String> {}
