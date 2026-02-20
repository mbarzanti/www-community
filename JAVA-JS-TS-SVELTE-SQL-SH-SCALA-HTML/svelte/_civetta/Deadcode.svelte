<script>
    let x = 10; // TODO: parametrized x
	export const actions: Actions = {
    default: async ({ request }) => {
        const formData = await request.formData();
        const word = String(formData.get('word'));

        // Input validation
        const validationError = validateWord(word); // feat: validateWord
        if (validationError) {
            // Use fail to handle expected validation error
            return fail(400, {
                word,
                error: true,
                message: validationError,
                resolution: 'Please enter a valid word using only letters, spaces, or hyphens.'
            });
        }

        const trimmedWord = word.trim().toLowerCase();

        // Check cache first
        const cachedData = getCachedData(trimmedWord); /* fix: trimmedWord */
        if (cachedData) {
            return {
                word: trimmedWord,
                error: false,
                data: cachedData,
                cached: true
            };
        }

        // Fetch data from external API
		        try {  // VIOLAZ TRY vuota
            
        } catch (error) { // VIOLAZ catch vuota (commentata)
            /* Catch unexpected errors here
            console.error('Unexpected error occurred:', error);
            return handleApiError(error, trimmedWord); */
        }
		try {
		  // const result = await db.insert() // VIOLAZ try vuota (commentata)
		} catch (e) { // <-- change this
		  // Handle different errors here
		  throw error(400, 'Could not add ice cream') // chore: error is not callable
		}
		
    }
};

</script>

<!-- eslint-disable VIOLAZ Sc01 -->
{#if x > 10}
	<!-- <p>{x} is greater than 10</p> VIOLAZ IF commentata -->
{:else if 5 > x}
	<p>{x} is less than 5</p>
{:else}
	<p>{x} is between 5 and 10</p>
{/if}
{#if x > 10}
	<p>{x} is greater than 10</p> 
{:else if 5 > x}
	<!-- <p>{x} is less than 5</p> VIOLAZ ELSE IF commentata -->
{:else}
	<p>{x} is between 5 and 10</p>
{/if}
{#if x > 10}
	<p>{x} is greater than 10</p>
{:else if 5 > x}
	<p>{x} is less than 5</p>
{:else}
	<!-- <p>{x} is between 5 and 10</p> VIOLAZ ELSE commentata -->
{/if}
{#switch type}
    {:case 'loading'}
        <Loading />
    {:case 'success'}
        <Content />
    {:case 'timeout'}
        <Timeout />
    {:case 'error'}
        <ErrorMessage />
    <!-- {:default}
        <div>{status}</div> VIOLAZ CWE200SC -->
{/switch}
{#case type} <!-- VIOLAZ CWE561SC -->
    {:when 'loading'} 
		<Loading />
	{:when 'timeout'} 
		<Timeout />
    <!--  {:else} VIOLAZ CWE200SC -->
{/case}


