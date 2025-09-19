# Integración API de Perfil de Usuario

## 🔗 **Configuración de la API**

### **Endpoint de Perfil**
- **URL**: `http://localhost:8080/api/user-profiles/by-user/{userId}`
- **Método**: `GET`
- **Headers**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {token}`

### **Estructura de Respuesta**
```json
{
  "id": "d1a5c790-e03c-4982-8e49-c7ff318a90d6",
  "userId": "c406dd59-9de2-481a-b09c-a24ac3733054",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "García",
  "alias": "aliceg",
  "birthDate": "1995-03-12",
  "bio": "Amante de la fotografía y el senderismo. Siempre con una cámara a mano.",
  "avatarUrl": "https://cdn.example.com/avatars/alice.jpg",
  "createdAt": "2025-09-18T22:37:46.1078Z",
  "updatedAt": "2025-09-18T22:37:46.1078Z"
}
```

## 🧩 **Componentes Actualizados**

### **ProfileService**
- **Método `getUserProfile(userId)`**: Obtiene perfil por ID de usuario
- **Método `updateUserProfile(userId, data)`**: Actualiza perfil de usuario
- **Manejo de errores**: Captura y formatea errores de API
- **Autenticación**: Incluye token JWT en headers

### **AuthService**
- **Método `getCurrentUserId()`**: Extrae userId del token JWT
- **Decodificación JWT**: Obtiene el campo `sub` que contiene el userId

### **Profile Component**
- **Carga real**: Obtiene perfil de la API al montar
- **Estados de carga**: Loading, error y éxito
- **Modo de edición**: Formulario para actualizar datos
- **Validación**: Campos requeridos y formatos
- **Avatar**: Muestra imagen o inicial del nombre

## 📊 **Datos Mostrados**

### **Información del Perfil**
- ✅ **Nombre**: `firstName` + `lastName`
- ✅ **Alias**: `alias` con @
- ✅ **Email**: `email` (solo lectura)
- ✅ **Fecha de Nacimiento**: `birthDate` formateada
- ✅ **Biografía**: `bio` en textarea
- ✅ **Avatar**: `avatarUrl` o inicial del nombre

### **Campos Editables**
- **firstName**: Nombre del usuario
- **lastName**: Apellido del usuario
- **alias**: Alias único
- **birthDate**: Fecha de nacimiento
- **bio**: Biografía personal
- **avatarUrl**: URL de la imagen de perfil

## 🔄 **Flujo de Datos**

```
1. Usuario accede a la pestaña "Perfil"
2. Profile component se monta
3. authService.getCurrentUserId() → Extrae userId del JWT
4. profileService.getUserProfile(userId) → API
5. Mostrar datos en la interfaz
6. Usuario hace clic en "Editar Perfil"
7. Modo de edición activado
8. Usuario modifica campos
9. profileService.updateUserProfile() → API
10. Recargar datos actualizados
```

## 🛠️ **Funcionalidades Implementadas**

### **Carga de Perfil**
- ✅ Obtener perfil de API real
- ✅ Extraer userId del token JWT
- ✅ Estados de carga y error
- ✅ Mostrar avatar o inicial

### **Edición de Perfil**
- ✅ Modo de edición con formulario
- ✅ Validación de campos
- ✅ Guardar cambios en API
- ✅ Cancelar y recargar datos originales

### **Interfaz de Usuario**
- ✅ Header con avatar y nombre completo
- ✅ Campos organizados en grid
- ✅ Botones de acción (Editar/Guardar/Cancelar)
- ✅ Feedback visual de estados

## 🧪 **Pruebas**

### **Para Probar la Integración**

1. **Asegúrate de que el backend esté corriendo** en `http://localhost:8080`

2. **Haz login** con credenciales válidas

3. **Ve a la pestaña "Perfil"**

4. **Verifica que se cargue el perfil** de la API

5. **Haz clic en "Editar Perfil"**

6. **Modifica algunos campos** y guarda

### **Verificar en DevTools**

1. **Network tab**: Ver peticiones a `/api/user-profiles/by-user/{userId}`
2. **Console**: Logs de carga y actualización
3. **Response**: Datos del perfil de la API

## 🔍 **Debugging**

### **Errores Comunes**

1. **401 Unauthorized**: Token expirado o inválido
2. **404 Not Found**: Perfil no encontrado
3. **500 Internal Server Error**: Error del servidor
4. **Network Error**: Backend no disponible

### **Logs Útiles**

```javascript
// En Profile component
console.log('Cargando perfil para userId:', userId);
console.log('Perfil cargado:', profileData);

// En ProfileService
console.log('Actualizando perfil:', profileData);
console.log('Perfil actualizado:', response);
```

## 🚀 **Próximas Mejoras**

### **Funcionalidades Pendientes**
1. **Subida de Avatar**: Upload de imágenes
2. **Validación Avanzada**: Validación de alias único
3. **Previsualización**: Vista previa del avatar
4. **Historial**: Historial de cambios
5. **Configuración**: Configuraciones de privacidad

### **Optimizaciones**
1. **Cache**: Cachear datos del perfil
2. **Optimistic Updates**: Actualizar UI antes de API
3. **Debounce**: Para validaciones en tiempo real
4. **Lazy Loading**: Cargar avatar bajo demanda

## 📝 **Notas Técnicas**

### **Estructura de Datos**
```javascript
// Perfil transformado
{
  firstName: "Alice",
  lastName: "García",
  email: "alice@example.com",
  alias: "aliceg",
  birthDate: "1995-03-12",
  bio: "Amante de la fotografía...",
  avatarUrl: "https://cdn.example.com/avatars/alice.jpg"
}
```

### **Manejo de Errores**
- **API Profile**: Error al cargar perfil
- **JWT Decode**: Error al extraer userId
- **Network**: Mensaje de error genérico
- **Validation**: Errores de formulario

### **Estados del Componente**
- **Loading**: Skeleton mientras carga
- **Error**: Mensaje de error con icono
- **Success**: Formulario con datos
- **Editing**: Modo de edición activado

### **Extracción de userId**
```javascript
// Del token JWT
const decoded = authService.decodeToken(token);
const userId = decoded.sub; // Campo 'sub' contiene el userId
```

## 🔐 **Seguridad**

### **Autenticación**
- **Token JWT**: En todas las peticiones
- **Validación**: Token no expirado
- **Autorización**: Solo el propio usuario puede editar su perfil

### **Validación**
- **Campos requeridos**: firstName, lastName, alias
- **Formatos**: Email, fecha, URL
- **Sanitización**: Limpiar datos de entrada
