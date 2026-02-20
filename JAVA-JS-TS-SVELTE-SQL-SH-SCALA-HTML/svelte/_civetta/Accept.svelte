<script>
import { page } from "$app/stores";
// user input
let href = $page.url.searchParams.get("href") ?? "https://example.com"; // href UNTRUSTED
</script>
<noscript>
  <a href={href}>test</a> <!-- VIOLAZ -->
</noscript>

<script>
import { page } from "$app/stores";
// user input
let href = $page.url.searchParams.get("href") ?? "https://example.com"; // href UNTRUSTED
</script>
<noscript>
  <a href={href}>test</a> <!-- VIOLAZ -->
</noscript>


<script context="module">
    export function load() {} // VIOLAZ
    export const load = () => {} // VIOLAZ
</script>

import('svelte/internal'); // VIOLAZ
import('svelte/internal/disclose-version'); // VIOLAZ
export * from 'svelte/internal'; // VIOLAZ
export { listen } from 'svelte/internal'; // VIOLAZ
export * from 'svelte/internal/server'; // VIOLAZ


<script>
	let count = $state(0);
	let message = $state('hello');
	$inspect(count, message); // VIOLAZ
let count = $state(0);
	$inspect(count).with((type, count) => { // VIOLAZ
		if (type === 'update') {
			debugger; // or `console.trace`, or whatever you want
		}
	});
$effect(() => {
		$inspect.trace(); // VIOLAZ
		doSomeWork();
	});
</script>


<script>
  import myStore from './my-stores';
  const unsubscribe = myStore.subscribe(() => {}); //OK
  myStore.subscribe(() => {}); // VIOLAZ
</script>


{@debug user} <!--VIOLAZ -->
{@debug user1, user2, user3} <!--VIOLAZ -->
{@debug} <!--VIOLAZ -->


<script>
  window.addEventListener('resize', handler); <!--VIOLAZ -->
</script>


<button type="button">Hello World</button> <!--OK -->
<button type="submit">Hello World</button> <!--OK -->
<button type="reset">Hello World</button> <!--OK -->
<button>Hello World</button> <!--VIOLAZ -->
<button type="">Hello World</button> <!--VIOLAZ -->
<button type="foo">Hello World</button> <!--VIOLAZ -->


<a href="http://example.com" target="_blank" rel="noopener noreferrer">link</a> <!--OK -->
<a href="http://example.com" target="_blank">link</a> <!--VIOLAZ -->

<script>
</script>
{@html foo} <!--VIOLAZ -->

<script>
  import { writable, get } from 'svelte/store';
  const storeValue = writable('world'); // storeValue è writeable
  const color = writable('red'); // color è writable
  $: message = 'Hello ${$storeValue}'; //OK storeValue inizia con $
  $: message = 'Hello ${ storeValue}'; // VIOLAZ
</script>
<p>{$storeValue}</p> <!--OK storeValue inizia con $ -->
<p>{get(storeValue)}</p> <!--OK storeValue nella get -->
<p class={$storeValue} /> <!--OK storeValue inizia con $ -->
<p style:color={$color} /> <!--OK color inizia con $ -->
<MyComponent prop="Hello {$storeValue}" /> <!--OK storeValue inizia con $ -->
<MyComponent bind:this={$storeValue} /> <!--OK storeValue inizia con $ -->
<MyComponent --style-props={$storeValue} /> <!--OK storeValue inizia con $ -->
<MyComponent {...$storeValue} /> <!--OK storeValue inizia con $ -->
<p>{storeValue}</p>  <!--VIOLAZ -->
<p class={storeValue} /> <!--VIOLAZ -->
<p style:color /> <!--VIOLAZ -->
<MyComponent prop="Hello {storeValue}" /> <!--VIOLAZ -->
<MyComponent bind:this={storeValue} /> <!--VIOLAZ -->
<MyComponent --style-props={storeValue} /> <!--VIOLAZ -->
<MyComponent {...storeValue} /> <!--VIOLAZ -->

<script>
  import { writable, readable } from 'svelte/store';
  readable(null, (set) => { // OK
    set(new Date());
    const interval = setInterval(() => set(new Date()), 1000);
    return () => clearInterval(interval);
  });
  readable(false, (set) => true); // OK
  writable(null, (set) => { // OK
    set(1);
    return () => {
      /* no more subscribers */
    };
  });
  writable(false, (set) => true); // OK
  readable(false, () => true); // VIOLAZ
  readable(false, (foo) => true); // VIOLAZ
  writable(false, () => true); // VIOLAZ
  writable(false,  (foo) => true); // VIOLAZ
</script>


<svelte:head> <!--OK -->
  <title>Valid</title>
</svelte:head>
<head> <!--VIOLAZ -->
  <title>Invalid</title>
</head>

<script>
  function foo() {
    /*  */
  }
  const bar = 42;
</script>
<button onclick={{ foo }} /> <!--VIOLAZ -->
<button onclick={foo} /> <!--OK -->
<button onclick={bar} /> <!--VIOLAZ bar è una costante-->


{#if show}
  <div bind:this={foo}>Foo</div> <!--VIOLAZ -->
{/if}


<script>

	import Nested from './Nested.svelte';
	import { resolvePath } from '@sveltejs/kit';
	import { base } from '$app/paths';
	
	import { writable, readable, derived } from 'svelte/store';
	const w1 = writable(false, () => {}); // OK
	const r1 = readable(false, () => {}); // OK
	const d1 = derived(a1, ($a1) => {}); // OK
	const w2 = writable(false, async () => {}); // VIOLAZ
	const r2 = readable(false, async () => {}); // VIOLAZ
	const d2 = derived(a1, async ($a1) => {}); // VIOLAZ


	const path = resolvePath('/blog/[slug]', { slug }); //VIOLAZ
	const path = base + resolvePath('/blog/[slug]', { slug }); // OK
	
	declare namespace svelte.JSX {
    interface HTMLProps<T> {
        onclick_outside?: (e: CustomEvent) => void; // VIOLAZ se browser con versione nella lista sopra
    }
	
	throw error(500, 'something went wrong');
	error(500, 'something went wrong');
	redirect(307, '/b');

	}

	cookies.set( // VIOLAZ manca path:
					'auth', '42',
					{
						httpOnly: true, 
					},
	 );
	cookies.set(name, value, { path: '/' , httpOnly: true}); // OK
	event.cookies.delete("session"); // VIOLAZ manca path:
	const jwt = cookie.serialize("jwt", json, {  // VIOLAZ manca path:
		httpOnly: true,
	});
	res.cookie("accessToken", accessToken, { // VIOLAZ manca path:
	  httpOnly: true,
	  secure: true, 
	  sameSite: 'none',
	  maxAge: 15 * 60 * 1000,
	});


cookies.set( // VIOLAZ manca path:
                'auth', '42',
                {
                    maxAge: 60 * 60 * 24 * 365,
                    httpOnly: false,  // VIOLAZ
                },
 );
return {
    headers: {
      "set-cookie": 'token=${body.token}; path=/', // VIOLAZ
    },
    body,
  };
return {
    headers: {
      "set-cookie": 'token=""; path=/; HttpOnly', // VIOLAZ
    },
    body: {
      ok: true,
    },
  };
return {
    headers: {
      "set-cookie": 'token=""; path=/; HttpOnly=true', // OK
    },
    body: {
      ok: true,
    },
  };
res.cookie("accessToken", accessToken, {
  httpOnly: false, // VIOLAZ
  secure: true, 
  sameSite: 'none',
  maxAge: 15 * 60 * 1000,
});

return {
    headers: {
      "set-cookie": 'token=""; path=/; HttpOnly; expires=Thu, 01 Jan 1970 00:00:00 GMT', // VIOLAZ
    },
    body: {
      ok: true,
    },
  };
cookies.set( 
                'auth', '42',
                {
	path: '/',
	expires: ‘Thu, 01 Jan 1970 00:00:00 GMT',
                    maxAge: 60 * 60 * 24 * 365, // VIOLAZ
                    httpOnly: true,  
                },
 );
return {
headers: {
			'Set-Cookie': [
				'cookie1=123; Path=/; HttpOnly; SameSite=Strict; maxAge=60 * 60 * 24 * 365; Expires=${access_token_expires_in}}', // VIOLAZ
				'cookie2=456; Path=/; HttpOnly; SameSite=Strict; Expires=${refresh_token_expires_in}' // VIOLAZ
			],
			Location: '/'
		},
  };
res.cookie("accessToken", accessToken, {
  httpOnly: true,
  secure: true, 
  sameSite: Strict,
  expires: ‘Thu, 01 Jan 1970 00:00:00 GMT',
  maxAge: 15 * 60 * 1000, // VIOLAZ
});


</script>
<Nested let:count>
	<p>
		count in default slot - is available: {count}
	</p>
	<p slot="bar"> <!--VIOLAZ -->
		count in bar slot - is not available: {count}
	</p>
</Nested>

{#await promise}
	<p in:slide>Success</p> <!-- VIOLAZ dentro la await -->
{:then value}
	<!-- promise was fulfilled or not a Promise -->
	<p>The value is {value}</p>
{:catch error}
	<!-- promise was rejected -->
	<p>Something went wrong: {error.message}</p>
{/await}
{#if show}
	{#if success}
		<p in:slide>Success</p> <!-- VIOLAZ dentro la each-->
	{/each}
{/if}
{#if x}
	{#if y}
		<p transition:fade>fades in and out only when y changes</p> <!-- VIOLAZ dentro la if -->
		<p transition:fade|global>fades in and out when x or y change</p> <!-- VIOLAZ dentro la if -->
	{/if}
{/if}
<button onclick={() => visible = !visible}>toggle</button>
{#if visible} 
	<div transition:fade>fades in and out</div> <!--VIOLAZ dentro la if -->
{/if}
{#key i}
	<p in:typewriter={{ speed: 10 }}> <!--VIOLAZ dentro la key -->
		{messages[i] || ''}
	</p>
{/key}

<script>
	import { SvelteComponentTyped } from 'svelte'; // VIOLAZ
    import {onMount} from 'svelte';
    import {_} from 'svelte-i18n';
	import {page} from "$app/store";

    export let appState;
    export let nextStateEnabled;

	const dispatch = createEventDispatcher(); // VIOLAZ
	dispatch('click');
	element.classList.toggle('className'); // VIOLAZ se browser con versione nella lista sopra
    let ready = false;
    let accepted = false;

    onMount(() => {
        nextStateEnabled = false;
        ready = true;
    });

    $:nextStateEnabled = ready && accepted;

    function checkAttivazione() {
        attivazioneFlag = true ? !attivazioneFlag : attivazioneFlag;
        nextStateEnabled = false ? !attivazioneFlag : attivazioneFlag;
    }
    on:click={()=>{checkAttivazione()}}
	
	
	const action: Action = (node, params) => { let href = node; let p = params} // VIOLAZ
	const action: Action<HTMLElement, string> = (node, params) => { let href = node; let p = params } // Ok
	export function myAction(node: HTMLElement, parameter: Parameter): ActionReturn<undefined> { // VIOLAZ
	return {
		update: (updatedParameter) => {...},
		destroy: () => {...}
		};
	}

	onMount(
	async () => { // VIOLAZ
		const something = await foo();
           	() => {
		foo().then(something => {...});
		// ...
		return () => someCleanup();
	}
	}
);


cookies.set('session', user.entityId, {
            path: '/',
            httpOnly: true,
            sameSite: 'strict', // OK
            secure: !dev,
            maxAge: 60 * 60 * 24 * 30
        });
cookies.set('session', user.entityId, {
            path: '/',
            httpOnly: true,
            sameSite: 'false', // VIOLAZ
            secure: !dev,
            maxAge: 60 * 60 * 24 * 30
        });
res.cookie("accessToken", accessToken, {
  httpOnly: true,
  secure: true, 
  sameSite: 'none', // VIOLAZ
  maxAge: 15 * 60 * 1000,
});

cookies.set('accessToken', res.data.accessToken, { 
                             secure: false, // VIOLAZ
                             httpOnly: true, 
                             path: '/' 
});
res.cookie("accessToken", accessToken, {
  httpOnly: true,
  secure: false, // VIOLAZ
  sameSite: 'lax',
  maxAge: 15 * 60 * 1000,
});

</script>
<svelte:options tag="my-component" /> <!-- VIOLAZ -->
<slot name="foo" message="hello" /> <!-- VIOLAZ -->
<slot /> <!-- VIOLAZ -->

{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}
{#if ready}
    <div class="container">
        <form class="generic-form bordered-elements">
            <label class="control-label large-label form-title">
                {$_('pages.cgs.title')}
            </label>
            <div class="form-group form-group-lg form-row review-cgs-text">
                <div class="col-xs-12 col-sm-12">
                    <div class="form-control-plaintext">
                        {$_('pages.cgs.body')}
                    </div>
                </div>
            </div>
            <div class="review-checkbox-item" style="border-radius: 5px;">
                <div class="review-checkbox">
                    <input id="checkbox" type="checkbox"
                           bind:checked={accepted}>
                    <label for="checkbox"></label>
                </div>
                <div class="review-checkbox-text">
                    {$_('pages.cgs.read')}
                </div>
            </div>
        </form>
    </div>
{/if}
<style>
    .upper{
        font-size: 20px;
        text-transform: inherit;
    }
    label.control-label.large-label.form-title {
        text-transform: unset;
    }

    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }

    .form-control-plaintext{
        font-style: italic;
    }
    .review-checkbox-text{
        font-style: italic;
    }

    .review-cgs-text{
        color: #222427;
        font-size: 16px;
        font-style: italic;
        letter-spacing: 0;
        line-height: 24px;
    }
    .review-checkbox label {
        border-radius: 40%;
    }
    @media (max-width: 767px) {
        label.control-label.large-label.form-title {
            color: #222427!important;
            font-family: Texta;
            font-size: 18px!important;
            letter-spacing: 0!important;
            line-height: 24px!important;
            padding-left: 8vw!important;
            padding-bottom: .5rem!important;
            padding-top: .5rem!important;
            margin-bottom: .5rem;
        }

        .form-control-plaintext {
            font-style: italic;
            padding-left: 2vw;
            padding-right: 3vw;
        }

        .review-checkbox-item {
            margin-left: 5vw;
            margin-top: .6rem;
        }
    }
</style>

