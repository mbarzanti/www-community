<script>
	import { tweened } from 'svelte/motion';
	import { cubicOut } from 'svelte/easing';
	import { getContext } from 'svelte';
	import Stop from './Stop.svelte';

	export let series = [];
	export let style = 'default';
	export let rx = 2;
	export let ry = 2;
	export let width = null;
	export let height = null;
	export let textSize = null;
	export let addBackground = true;
	export let bgColor = null;

	const minOverallPerc = 0.001;
	const ts = new Date().getTime();
	const valStore = getContext('valStore');
	const maskId = 'tx_mask_' + ts + Math.floor(Math.random() * 999);
	const grId = 'pb_gradient_' + ts + Math.floor(Math.random() * 999);

	if(width == null) {
		//Default width when not specified
		width = 150;
	}

	if(height == null) {
		//Default height when not specified
		height = style == 'thin' ? 10 : 14;
	}

	if(textSize == null)
		textSize = style == 'thin' ? 40 : 70;

	let ypos = 0;
	if(style == 'thin') {
		rx = .2;
		ry = .2;
		ypos = 100 - height;
	}

	//Start with a number slightly greater than 0 to avoid divisions by zero when computing stops
	const overallPerc = tweened(minOverallPerc, {
		duration: 1000,
		easing: cubicOut
	});

	$: {
		overallPerc.set(series.reduce((a, s) => a + s.perc < 100 ? a + s.perc : 100, minOverallPerc));
	}

	let dominantBaseline = '';
	let dy = '0';
 	if(window.navigator.userAgent.indexOf('Trident') > -1 || window.navigator.userAgent.indexOf('Edge') > -1) {
		//Ugly workaround needed only in legacy mode to adjust the vertical positioning of the value
		//in IE/Edge (that don't support dominant-baseline)...
		dy = '-.4em';
	}
	else {
		dominantBaseline = 'central';
	}
</script>

<style>

	.progressbar-thin {
		overflow: visible;
	}

	.progress-bg {
		fill: #f1f1f1;
	}

	.progress-value-inverted {
		fill: #fff;
	}

</style>

<svg class="progressbar progressbar-{style}" width="{width}" height="30" xmlns="http://www.w3.org/2000/svg">
	<defs>
		<linearGradient id="{grId}">
			{#each series as serie}
				<Stop prevOffset={serie.prevOffset} offset={serie.offset} color={serie.color} cls="{serie.cls}" {overallPerc}/>
			{/each}
		</linearGradient>
	</defs>

	{#if style == 'thin'}
		{#if addBackground}
			<rect width="100" height="100%" x="0" y="0" fill={bgColor} class="progress-bg"></rect>
		{/if}
		<rect width="{$overallPerc}%" height="20px" x="0" y="0" fill="#0047bb"></rect>
	{:else}
		{#if addBackground}
			<rect width="100" height="100%" {rx} {ry} y="0" fill={bgColor} class="progress-bg"></rect>
		{/if}
		<rect width="{$overallPerc}%" height="100%" {rx} {ry} y="0" fill="#0047bb"></rect>

	{/if}
</svg>