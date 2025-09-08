description =
"""
Core domain layer:
- contains business logic (domain services/rules);
- contains domain objects (aggregates, value objects, domain events);
- domain events are used only inside of one bounded context (microservice);
- integration events are used as incoming events from other bounded context (microservices);
- any framework dependencies are not allowed here;
"""

applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
}