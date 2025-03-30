# sp-2-team-5: Movie Recommendation API

Jeg har lavet en Movie Recommendation API.
Jeg har brugt koden fra Andrés API Security Guide som udgangspunkt for mit projekt.

## Visionen for projektet

## Endpoints

API'ens URL er: https://movie.jcoder.dk/api

| Method | URL                     | Request Body (JSON)     | Response (JSON)                         | Roles  |
|--------|-------------------------|-------------------------|-----------------------------------------|--------|
| POST   | /auth/register          | `{"username": String,"password": String}` | `{"token": String, "username": String}` | ANYONE |
| POST   | /auth/login             | `{"username": String,"password": String}`                  | `{"token": String, "username": String}` | ANYONE |
| GET    | /movies                 | (empty)                 | `[movie,movie,...]`                     | USER   |
| PUT    | /movies/(id)            | `{"likes": Boolean}`    | (empty)                                 | USER   |
| DELETE | /movies/(id)            | (empty)                 | (empty)                                 | USER   |
| GET    | /movies/recommendations | (empty)                 | `[movie,movie,...]`                     | USER   |
| GET    | /movies/search          | `{"text": String}`      | `[movie,movie,...]`                     | USER   |
| GET    | /movies/search-open     | `{"text": String}`      | `[movie,movie,...]`                     | ANYONE |

```
movie =
{
    "id": Number (samme id som på TMDB),
    "title": String,
    "originalTitle": String,
    "releaseDate": [Number,Number,Number] (YYYY,MM,DD),
    "rating": Number (fra 0.0 til 10.0),
    "posterPath": String,
    "likes": Boolean
}

Boolean "likes" er:
 - TRUE hvis brugeren kan lide filmen
 - FALSE hvis brugeren ikke kan lide filmen
 - NULL hvis brugeren ikke har udtrykt sin holdning til filmen
```