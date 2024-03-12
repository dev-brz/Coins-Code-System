export function reSpyOnProperty<T, K extends keyof T>(object: T, property: K): jasmine.Spy<() => T[K]> {
  return Object.getOwnPropertyDescriptor(object, property)!.get as jasmine.Spy<() => T[K]>;
}
