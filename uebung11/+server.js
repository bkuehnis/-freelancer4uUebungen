import { error, json } from '@sveltejs/kit';
import { API_BASE_URL } from '$app/env/private';

export async function POST({ request, locals, fetch }) {
	if (!locals.jwt_token) error(401, 'Sign in to use the chat.');

	let chatRequest;
	try {
		chatRequest = await request.json();
	} catch {
		error(400, 'Invalid chat request.');
	}

	let response;
	try {
		response = await fetch(`${API_BASE_URL}/api/chat`, {
			method: 'POST',
			headers: {
				Authorization: `Bearer ${locals.jwt_token}`,
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(chatRequest)
		});
	} catch {
		error(502, 'Could not reach Freelancer4u.');
	}

	if (response.status === 401 || response.status === 403) error(401, 'Sign in again to use the chat.');
	if (!response.ok) error(502, 'The chat could not get a response.');
	return json(await response.json());
}
