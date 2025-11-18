<script>
  import { enhance } from "$app/forms";
  import { onMount } from "svelte";

  let { data } = $props();
  let { user, isAuthenticated } = data;

  let message = $state("");
  let messages = $state([
    { type: "bot", text: "Hallo! Wie kann ich dir helfen?" },
  ]);

</script>

{#if isAuthenticated && user.user_roles?.includes("admin")}
  <div class="chat-wrapper d-flex flex-column">
    <!-- CHAT MESSAGES -->
    <div
      class="chat-messages flex-grow-1 overflow-auto p-3 bg-light d-flex flex-column"
    >
      {#each messages as msg}
        {#if msg.type === "loading"}
          <div class="message bot align-self-start typing">
            <span></span><span></span><span></span>
          </div>
        {:else}
          <div
            class="message {msg.type} align-self-{msg.type === 'user'
              ? 'end'
              : 'start'}"
          >
            {msg.text}
          </div>
        {/if}
      {/each}
    </div>

    <!-- INPUT AREA -->
    <div class="chat-input p-3 border-top bg-white">
      <form
        method="POST"
        action="?/chat"
        class="d-flex"
        use:enhance={({ formData }) => {
          const text = formData.get("message");

          // Push user message
          messages.push({ type: "user", text });

          // Clear input
          message = "";

          // Bot typing animation
          messages.push({ type: "loading" });

          return async ({ result }) => {
            // Remove the loading bubble
            messages = messages.filter((m) => m.type !== "loading");

            if (result.type === "success" && result.data?.reply) {
              messages.push({ type: "bot", text: result.data.reply });
            } else {
              messages.push({
                type: "bot",
                text: "Fehler beim Serverkontakt.",
              });
            }
          };
        }}
      >
        <textarea
          name="message"
          class="form-control me-2"
          placeholder="Type your message..."
          bind:value={message}
          required
        ></textarea>

        <button type="submit" class="btn btn-primary"> Send </button>
      </form>
    </div>
  </div>
{/if}

<style>
  .chat-wrapper {
    height: 80vh;
    border: 1px solid #ddd;
    border-radius: 0.5rem;
    overflow: hidden;
  }

  .chat-messages {
    gap: 1rem;
    overflow-y: auto;
    overflow-anchor: auto;
    scroll-behavior: smooth;
  }

  .message {
    padding: 0.5rem 1rem;
    border-radius: 10px;
    max-width: 75%;
    white-space: pre-wrap;
  }

  .message.bot {
    background-color: #6c757d;
    color: #fff;
  }

  .message.user {
    background-color: #0d6efd;
    color: #fff;
  }

  /* Typing animation bubble */
  .message.typing span {
    height: 8px;
    width: 8px;
    margin: 0 3px;
    display: inline-block;
    background: #fff;
    border-radius: 50%;
    animation: blink 1.4s infinite both;
  }

  .message.typing span:nth-child(2) {
    animation-delay: 0.2s;
  }

  .message.typing span:nth-child(3) {
    animation-delay: 0.4s;
  }

  @keyframes blink {
    0% {
      opacity: 0.2;
    }
    20% {
      opacity: 1;
    }
    100% {
      opacity: 0.2;
    }
  }

  textarea {
    min-height: 52px;
  }
</style>