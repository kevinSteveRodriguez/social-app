# Integración API de Publicaciones

## 🔗 **Configuración de la API**

### **Endpoint de Publicaciones**
- **URL**: `http://localhost:8080/api/posts`
- **Método**: `GET`
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {token}`

### **Estructura de Respuesta**
```json
{
  "content": [
    {
      "id": "7115173e-248f-4127-87d3-c000465cb904",
      "userId": "b2b32201-040d-4334-9958-8302e119345b",
      "content": "Maratón de cine clásico este fin de semana",
      "mediaUrl": null,
      "likesCount": 11,
      "commentsCount": 1,
      "createdAt": "2025-09-18T18:36:54.801651Z",
      "updatedAt": "2025-09-18T18:36:54.801651Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "empty": true, "sorted": false, "unsorted": true },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 7,
  "size": 10,
  "number": 0,
  "numberOfElements": 7,
  "first": true,
  "empty": false
}
```

## 🧩 **Componentes Actualizados**

### **PostsService**
- **Método `getPosts()`**: Obtiene publicaciones paginadas
- **Método `createPost()`**: Crea una nueva publicación
- **Manejo de errores**: Captura y formatea errores de API
- **Autenticación**: Incluye token JWT en headers

### **UsersService**
- **Método `getUserById()`**: Obtiene información de usuario
- **Fallback**: Datos por defecto si no se puede obtener el usuario
- **Manejo de errores**: Graceful degradation

### **Posts Component**
- **Carga real**: Obtiene publicaciones de la API
- **Información de usuario**: Combina datos de posts y usuarios
- **Estados de carga**: Loading, error y éxito
- **Interacciones**: Like y comentarios (funcionales)

### **CreatePost Component**
- **API real**: Envía publicaciones al backend
- **Validación**: Contenido no vacío y límite de caracteres
- **Feedback**: Mensajes de éxito y error
- **Recarga**: Notifica al padre para recargar lista

## 📊 **Datos Mostrados**

### **Información Extraída**
- ✅ **Mensaje**: `post.content`
- ✅ **Usuario**: `post.alias` (directamente del JSON)
- ✅ **Fecha**: `post.createdAt` → Formato relativo

### **Información Adicional**
- **Likes**: `post.likesCount`
- **Comentarios**: `post.commentsCount`
- **ID**: `post.id` (para interacciones)

## 🔄 **Flujo de Datos**

```
1. Usuario accede al Dashboard
2. Posts component se monta
3. postsService.getPosts() → API
4. Transformar datos directamente (alias incluido)
5. Mostrar en UI
6. Usuario crea nueva publicación
7. postsService.createPost() → API
8. Recargar lista de publicaciones
```

## 🛠️ **Funcionalidades Implementadas**

### **Carga de Publicaciones**
- ✅ Obtener publicaciones de API real
- ✅ Usar alias directamente del JSON
- ✅ Transformar datos sin llamadas adicionales
- ✅ Mostrar solo: mensaje, alias, fecha
- ✅ Estados de carga y error

### **Crear Publicación**
- ✅ Enviar a API real con formato correcto
- ✅ Body: `{ "content": "...", "mediaUrl": null }`
- ✅ Validación de contenido
- ✅ Feedback visual
- ✅ Recarga automática de lista

### **Interacciones**
- ✅ Like (local, no persistido)
- ✅ Comentarios (placeholder)
- ✅ Compartir (placeholder)

## 🧪 **Pruebas**

### **Para Probar la Integración**

1. **Asegúrate de que el backend esté corriendo** en `http://localhost:8080`

2. **Haz login** con credenciales válidas

3. **Ve a la pestaña "Publicaciones"**

4. **Verifica que se carguen las publicaciones** de la API

5. **Crea una nueva publicación** y verifica que aparezca

### **Verificar en DevTools**

1. **Network tab**: Ver peticiones a `/api/posts`
2. **Console**: Logs de carga y creación
3. **Response**: Datos de la API

## 🔍 **Debugging**

### **Errores Comunes**

1. **401 Unauthorized**: Token expirado o inválido
2. **404 Not Found**: Endpoint no existe
3. **500 Internal Server Error**: Error del servidor
4. **Network Error**: Backend no disponible

### **Logs Útiles**

```javascript
// En Posts component
console.log('Cargando publicaciones...');
console.log('Publicaciones cargadas:', posts);

// En CreatePost component
console.log('Creando publicación:', { content });
console.log('Publicación creada:', response);
```

## 🚀 **Próximas Mejoras**

### **Funcionalidades Pendientes**
1. **Paginación**: Cargar más publicaciones
2. **Infinite Scroll**: Scroll infinito
3. **Filtros**: Filtrar por usuario o fecha
4. **Búsqueda**: Buscar en publicaciones
5. **Imágenes**: Mostrar mediaUrl si existe

### **Optimizaciones**
1. **Cache**: Cachear publicaciones
2. **Debounce**: Para búsquedas
3. **Virtual Scrolling**: Para listas grandes
4. **Lazy Loading**: Cargar imágenes bajo demanda

## 📝 **Notas Técnicas**

### **Estructura de Datos**
```javascript
// Post transformado
{
  id: "uuid",
  content: "Mensaje del post",
  author: {
    id: "user-uuid",
    alias: "ernesto123"
  },
  createdAt: Date,
  likesCount: number,
  commentsCount: number
}
```

### **Manejo de Errores**
- **API Posts**: Error al cargar publicaciones
- **API Users**: Fallback a datos por defecto
- **Network**: Mensaje de error genérico
- **Validation**: Errores de formulario

### **Estados del Componente**
- **Loading**: Skeleton mientras carga
- **Error**: Mensaje de error con icono
- **Success**: Lista de publicaciones
- **Empty**: Mensaje cuando no hay posts

## 📝 **API de Creación de Publicaciones**

### **Endpoint**
- **URL**: `http://localhost:8080/api/posts`
- **Método**: `POST`
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {token}`

### **Estructura de la Petición**
```json
{
  "content": "¡Hola mundo! Este es mi primer post.",
  "mediaUrl": null
}
```

### **Respuesta Exitosa (200)**
```json
{
  "id": "uuid-generado",
  "userId": "user-uuid",
  "content": "¡Hola mundo! Este es mi primer post.",
  "mediaUrl": null,
  "likesCount": 0,
  "commentsCount": 0,
  "createdAt": "2025-09-18T18:36:54.801651Z",
  "updatedAt": "2025-09-18T18:36:54.801651Z",
  "alias": "usuario_alias"
}
```

### **Parámetros**
- **content** (string, requerido): Contenido de la publicación
- **mediaUrl** (string, opcional): URL de imagen o video (por defecto: null)
