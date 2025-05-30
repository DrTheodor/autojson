// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

import starlightImageZoom from 'starlight-image-zoom'

// https://astro.build/config
export default defineConfig({
	site: 'https://theo.is-a.dev',
	base: '/autojson',
	integrations: [
		starlight({
			title: 'AutoJSON',
			social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/DrTheodor/autojson' }],
			sidebar: [
				{ label: 'Reference', link: '/reference', badge: { text: 'JavaDocs', variant: 'tip' } },
				{
					label: 'Guides',
					items: [
						// Each item here is one entry in the navigation menu.
						{ label: 'Getting Started', slug: 'guides/getting-started' },
						{ label: 'Annotations', slug: 'guides/annotations' },
						{ label: 'Schemas', slug: 'guides/schemas' },
						{ label: 'Layers', slug: 'guides/layers' },
						{ label: 'Logger', slug: 'guides/logger' },
						{ label: 'Tips & Tricks', slug: 'guides/tips-n-tricks' },
					],
				},
				{ label: 'Benchmark', slug: 'benchmark', badge: { text: 'JMH', variant: 'note' } }
			],
			editLink: {
				baseUrl: 'https://github.com/DrTheodor/autojson/edit/main/docs/'
			},
			plugins: [
				starlightImageZoom(),
			],
		}),
	],
});
