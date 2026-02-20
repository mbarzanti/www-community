// ReactToshell
// Redwood SDK

import { defineApp } from "rwsdk/worker";
import { route, render } from "rwsdk/router";
import MyReactPage from "@app/pages/MyReactPage";
"use server"; // VIOLAZ
import { db } from "@/db";

export async function getUsers() {
  const users = await db.users.findAll();
  return users;
}

// UserList.tsx (React server component)
import { getUsers } from "./users";

export default async function UsersPage() {
  const users = await getUsers();
  return (
    <div>
      <ul>
        {users.map((user) => (
          <li key={user.id}>{user.name}</li>
        ))}
      </ul>
    </div>
  );
}

export default defineApp([
  render(Document, [
    route("/", () => new Response("Hello, World!")),
    route("/ping", function () {
      return <h1>Pong!</h1>;
    }),
    route("/react", MyReactPage)
    route("/docs", async () => {
      return new Response(null, {
        status: 301,
        headers: {
          "Location": "https://docs.rwsdk.com",
        },
      });
    }),
    route("/sitemap.xml", async () => {
      return new Response(sitemap, {
        status: 200,
        headers: {
          "Content-Type": "application/xml",
        },
      });
    }),
    route("/robots.txt", async () => {
      const robotsTxt = `User-agent: *
        Allow: /
        Disallow: /search
        Sitemap: https://rwsdk.com/sitemap.xml`;

      return new Response(robotsTxt, {
        status: 200,
        headers: {
          "Content-Type": "text/plain",
        },
      });
    }),
  ]),
]);