package users.register

import rego.v1

default allow := false

allow if user_is_admin

allow if {
	some grant in user_is_granted
	input.action == grant.action
}

user_is_admin if data.user_roles[input.email] == "admin"

user_is_granted contains grant if {
	role := data.user_roles[input.email]
	grant := data.role_grants[role]
}