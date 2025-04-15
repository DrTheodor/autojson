// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

// https://astro.build/config
export default defineConfig({
	site: 'https://theo.is-a.dev',
	base: '/autojson',
	integrations: [
		starlight({
			title: 'AutoJSON',
			social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/withastro/starlight' }],
			sidebar: [
				{
					label: 'Guides',
					items: [
						// Each item here is one entry in the navigation menu.
						{ label: 'Example Guide', slug: 'autojson/guides/example' },
					],
				},
				{
					label: 'Reference',
					autogenerate: { directory: 'autojson/reference' },
				},
			],
			editLink: {
				baseUrl: 'https://github.com/DrTheodor/autojson/edit/main/docs/'
			}
		}),
	],
});
