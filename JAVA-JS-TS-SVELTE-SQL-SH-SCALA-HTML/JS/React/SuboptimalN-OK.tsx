/*
Pattern #1: Internal State in JSX
Instead of using the ‘Destroy and Recreate’ anti-pattern, in this ‘correct but suboptimal’ approach, we will use ‘re-rendering.’

Re-rendering refers to calling the react functional component again with the hooks intact across function calls. Note that in ‘Destroy and Recreate,’ all the hooks are destroyed first and then recreated from scratch.

To implement ‘re-rendering,’ useEffect and useState will be used in tandem. The initial value for the useState can be set to null or undefined and an actual value will be computed and assigned to it once useEffect has run. In this pattern, we are circumventing the lack of dependency array in useState by using useEffect.

Specifically, notice how we have moved the total character count computation into JSX (line 44) in the Suboptimal.tsx and we are using props (line 33) as a dependency in the useEffect (line 25).
*/
import { useCallback, useEffect, useState } from "react";
import { Navigation } from "../components/Navigation";
import { useGetArticles } from "../hooks/useGetArticles";
import { useGetEmoji } from "../hooks/useGetEmoji";
import { Articles } from "../types";

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    background: "#FEFCE8",
    height: "100%",
    display: "grid",
    gridTemplateColumns: "10rem auto"
  },
  content: {}
};

const ArticleContent: React.FC<{
  article: Articles["articles"]["0"];
}> = (props) => {
  // Step 2. fetch emotion map from backend
  const emotions = useGetEmoji();

  // Step 3, set emotion once we get emotion map from backend
  const [emotion, setEmotion] = useState<string>("");
  useEffect(() => {
    if (emotions) {
      setEmotion(
        emotions["stickers"][
          props.article.text.length + props.article.title.length
        ]
      );
    }
  }, [emotions, props]);

  return (
    <div>
      <div>
        <h2>{props.article.title}</h2>
        <div>{props.article.text}</div>
      </div>
      <h3
        dangerouslySetInnerHTML={{
          __html: `Total Length ${
            props.article.text.length + props.article.title.length
          } ${emotion}`
        }}
      />
    </div>
  );
};

const Suboptimal: React.FC = () => {
  const articles = useGetArticles();
  const [currentArticle, setCurrentArticle] = useState<
    Articles["articles"]["0"] | null
  >();
  const onClickHandler = useCallback((article) => {
    setCurrentArticle(article);
  }, []);
  return (
    <div style={styles.container}>
      <Navigation articles={articles} onClickHandler={onClickHandler} />
      <div style={styles.content}>
        {currentArticle ? <ArticleContent article={currentArticle} /> : null}
      </div>
    </div>
  );
};

export default Suboptimal;