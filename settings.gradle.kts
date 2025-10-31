rootProject.name = "delivery"

// Core
include("core:domain")
include("core:application")

// In/Primary/Drive adapters
include("api:http")
include("api:kafka")
include("api:scheduler")

// Out/Secondary/Driven adapters
include("infrastructure:orm")
include("infrastructure:grpc")
include("infrastructure:kafka")
include("infrastructure:persistence:in-memory")

// Configuration of whole application
include("configuration")

// Architecture tests of dependencies between modules of whole application
include("architecture-tests")