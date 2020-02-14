# Create anuncio

```
curl -d '{"nombre":"michel", "asunto":"Vendo moto", "comentario":"Bien barata"}'\
    -H "Content-Type: application/json" -X POST http://localhost:3000/api/anuncios/
```