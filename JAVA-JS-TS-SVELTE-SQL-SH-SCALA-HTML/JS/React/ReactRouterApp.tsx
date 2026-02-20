/*
Enabling React Server Components
https://www.epicreact.dev/react-routers-take-on-react-server-components-4bj7q
The first step is to enable RSC in your React Router app. You'll need to install two plugins:

The React Router RSC plugin from @react-router/dev/vite
The RSC plugin from @vitejs/plugin-rsc
Here's how to update your vite.config.ts:
*/
import {
	reactRouter,
	// unstable_reactRouterRSC as reactRouterRSC,
} from '@react-router/dev/vite'
import tailwindcss from '@tailwindcss/vite'
// import rsc from '@vitejs/plugin-rsc'
import { defineConfig } from 'vite'
import devtoolsJson from 'vite-plugin-devtools-json'
import tsconfigPaths from 'vite-tsconfig-paths'

export default defineConfig({
	server: {
		port: process.env.PORT ? Number(process.env.PORT) : undefined,
	},
	plugins: [
		tailwindcss(),
		tsconfigPaths(),
		// Replace reactRouter() with:
		// reactRouterRSC(),
		// rsc(),
		devtoolsJson(),
	],
})
'use server'

export async function setIsFavorite(formData: FormData) {
	// Simulate API call delay
	await new Promise((resolve) => setTimeout(resolve, 50))

	const movieId = Number(formData.get('id'))
	const isFavorite = formData.get('isFavorite') === 'true'
	// Update the movie's favorite status
	const movie = movies.find((m) => m.id === movieId)
	if (movie) {
		movie.isFavorite = isFavorite
	}
}