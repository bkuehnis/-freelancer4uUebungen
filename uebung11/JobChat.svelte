<script>
	import { tick } from 'svelte';

	let open = $state(false);
	let messages = $state([]);
	let draft = $state('');
	let pending = $state(false);
	let failure = $state('');
	let composer = $state();
	let launcher = $state();
	let transcript = $state();

	async function showChat() {
		open = true;
		await tick();
		composer?.focus();
	}

	async function closeChat() {
		open = false;
		await tick();
		launcher?.focus();
	}

	async function scrollToLatest() {
		await tick();
		transcript?.scrollTo({ top: transcript.scrollHeight });
	}

	async function send(message) {
		const content = message.trim();
		if (!content || pending) return;

		pending = true;
		failure = '';
		draft = '';
		const next = [...messages, { role: 'user', content }];
		messages = next;
		await scrollToLatest();

		try {
			const response = await fetch('/api/chat', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
				body: JSON.stringify(next.slice(-21).map((turn) => ({
					role: turn.role,
					content: turn.content.slice(0, 8000)
				})))
			});
			const result = await response.json();
			if (!response.ok) throw new Error(result.message || 'Could not send your message.');
			messages = [...next, { role: 'assistant', content: result.reply }];
		} catch (problem) {
			messages = next.slice(0, -1);
			draft = content;
			failure = problem.message;
		} finally {
			pending = false;
			await scrollToLatest();
		}
	}
</script>

{#if open}
	<section id="job-chat" class="chat card shadow" aria-labelledby="job-chat-title">
		<header class="card-header d-flex justify-content-between align-items-center">
			<strong id="job-chat-title">Job assistant</strong>
			<button type="button" class="btn-close" aria-label="Close chat" onclick={closeChat}></button>
		</header>

		<div class="transcript" role="log" aria-label="Chat messages" bind:this={transcript}>
			{#if messages.length === 0}
				<p>What kind of job are you looking for?</p>
				<button type="button" class="btn btn-sm btn-outline-primary"
					onclick={() => send('Find Java jobs')}>Find Java jobs</button>
			{/if}
			{#each messages as message}
				<div class="bubble" class:outgoing={message.role === 'user'}>
					<span class="visually-hidden">{message.role === 'user' ? 'You' : 'Assistant'}: </span>
					{message.content}
				</div>
			{/each}
			{#if pending}<p class="text-body-secondary mb-0">Thinking…</p>{/if}
		</div>

		<form class="card-footer" onsubmit={(event) => { event.preventDefault(); send(draft); }}>
			{#if failure}<p class="text-danger mb-2" role="alert">{failure}</p>{/if}
			<div class="input-group">
				<input class="form-control" aria-label="Message" placeholder="Ask about jobs"
					maxlength="8000" bind:value={draft} bind:this={composer} readonly={pending} />
				<button type="submit" class="btn btn-primary" disabled={pending || !draft.trim()}>Send</button>
			</div>
		</form>
	</section>
{:else}
	<button class="launcher btn btn-primary shadow" type="button" bind:this={launcher}
		onclick={showChat}>Chat about jobs</button>
{/if}

<style>
	.launcher, .chat {
		position: fixed;
		right: 1rem;
		bottom: 1rem;
		z-index: 1030;
	}

	.chat {
		width: min(23rem, calc(100vw - 2rem));
		height: min(29rem, calc(100dvh - 2rem));
	}

	.transcript {
		flex: 1;
		overflow-y: auto;
		padding: 1rem;
	}

	.bubble {
		width: fit-content;
		max-width: 95%;
		margin-bottom: .65rem;
		padding: .55rem .75rem;
		border-radius: .75rem;
		background: var(--bs-tertiary-bg);
		white-space: pre-wrap;
		overflow-wrap: anywhere;
	}

	.outgoing {
		margin-left: auto;
		color: white;
		background: var(--bs-primary);
	}
</style>
