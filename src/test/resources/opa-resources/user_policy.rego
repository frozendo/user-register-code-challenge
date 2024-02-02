package users.register

import rego.v1

default allow := false

allow if user_is_admin

allow if {
	some grant in user_is_granted
	input.action == grant.action
}

user_is_admin if {
	some email in get_email_by_user_token
	data.user_roles[email] == "admin"
}

user_is_granted contains grant if {
	some email in get_email_by_user_token
	role := data.user_roles[email]
	grant := data.role_grants[role]
}

get_email_by_user_token contains email if {
	email := data.user_token[input.token]
}