<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";
  import { jwt_token, isAuthenticated, user } from "../../store";

  const api_root = page.url.origin;

  let message = $state();
  let messages = $state([
    { type: "bot", text: "Hallo! Wie kann ich dir helfen?" },
  ]);

  onMount(() => {});

  function chat() {
    // Add user's message
    messages.push({ type: "user", text: message });

    //add message like <div class="message user align-self-end">message</div>
    let query = "?message=" + encodeURIComponent(message);
    message = ""; // Clear the input field
    var config = {
      method: "get",
      url: api_root + "/api/chat" + query,
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + $jwt_token,
      },
    };
    console.log(config);

    axios(config)
      .then(function (response) {
        console.log(response.data);
        messages.push({ type: "bot", text: response.data });
      })
      .catch(function (error) {
        alert("Error while chatting");
        console.log(error);
      });
  }
</script>

{#if $isAuthenticated && $user.user_roles && $user.user_roles.includes("admin")}
  <div class="chat-wrapper d-flex flex-column">
    <div
      class="chat-messages flex-grow-1 overflow-auto p-3 bg-light d-flex flex-column"
    >
      {#each messages as msg}
        <div
          class="message {msg.type} align-self-{msg.type === 'user'
            ? 'end'
            : 'start'}"
        >
          {msg.text}
        </div>
      {/each}
    </div>

    <!-- Input area -->
    
    <!-- Input area -->
    <div class="chat-input p-3 border-top bg-white">
      <form class="d-flex">
        <textarea
          class="form-control me-2"
          placeholder="Type your message..."
          bind:value={message}
        ></textarea>
        <button class="btn btn-primary" onclick={chat}>Send</button>
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
  }

  .message {
    padding: 0.5rem 1rem;
    border-radius: 10px; /* pill shape */
    max-width: 75%;
    white-space: pre-wrap;
  }

  .message.bot {
    background-color: #6c757d; /* Bootstrap secondary */
    color: #fff;
  }

  .message.user {
    background-color: #0d6efd; /* Bootstrap primary */
    color: #fff;
  }

  textarea {
    min-height: 52px; /* Match input height */
  }

  textarea:focus {
    outline: none;
  }
</style>
