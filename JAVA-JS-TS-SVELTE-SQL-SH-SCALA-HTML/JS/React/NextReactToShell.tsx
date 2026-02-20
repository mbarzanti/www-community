/*
Next.js 12 introduced new improvements to the Image Component with a temporary import: next/future/image. These improvements included less client-side JavaScript, easier ways to extend and style images, better accessibility, and native browser lazy loading.

In version 13, this new behavior is now the default for next/image.

There are two codemods to help you migrate to the new Image Component:

next-image-to-legacy-image codemod: Safely and automatically renames next/image imports to next/legacy/image. Existing components will maintain the same behavior
https://nextjs.org/docs/app/guides/migrating/app-router-migration
*/
import Image from 'next/image'  
import Image from 'next-image-experimental' 
"use client"; // VIOLAZ React4Shell exposed 
export default function Page() {
  return (
    <Image
      src="/profile.png"
      width={500}
      height={500}
      alt="Picture of the author"
    />
  )
}