import axios from "axios";
import { error } from "@sveltejs/kit";
// Load environment variables from .env file for local development
import 'dotenv/config';
const API_BASE_URL = process.env.API_BASE_URL; // defined in frontend/.env

export const actions = {
  chat: async ({ request, locals }) => {

    const jwt_token = locals.jwt_token;
    if (!jwt_token){ throw error(401, "Authentication required") };

    const data = await request.formData();
    const message = data.get("message");

    try {
      const res = await axios.get(
        `${API_BASE_URL}/api/chat?message=${encodeURIComponent(message)}`,
        {
          headers: {
            Authorization: `Bearer ${jwt_token}`
          }
        }
      );

      return { reply: res.data };  // <-- sent back to client enhance()
    } catch (err) {
      console.error("Chat backend error:", err);
      return { reply: "Error occurred while contacting the server." };
    }
  }
};
