rootProject.name = "delivery"

// Core
include("core:domain")
include("core:application")

// In/Primary/Drive adapters
include("api:http")
include("api:kafka")
include("api:scheduler")
