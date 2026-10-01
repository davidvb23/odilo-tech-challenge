# NOTES

## Posiciones sobre las cinco preguntas
1. **Reserva no recogida:** caduca, sin penalización; el ejemplar pasa al siguiente en espera o vuelve a DISPONIBLE. Se comprueba de forma perezosa, al operar sobre ese título.
2. **Cola de un título que ya tengo prestado:** se rechaza.
3. **Renovar siendo el único en espera:** fuera de alcance, no hay renovaciones.
4. **Vencimiento:** solo si `hoy > fechaVencimiento` (`LocalDate`), con un único `Clock` inyectado en la zona de la biblioteca. Devolver a las 23:58 del día de vencimiento no genera multa.
5. **Multas:** bloquean solo el préstamo; no bloquean devolver, unirse a una cola ni recoger una reserva ya asignada.

## 1. Decisiones
**A. Un `ReentrantLock` por título alrededor de toda la decisión.** La race está en el check-then-act de `prestar`: dos hilos ven el mismo ejemplar DISPONIBLE y ambos lo prestan. Otra: `devolver` frente a `prestar`, donde alguien se salta la cola y se lleva el ejemplar de la primera reserva. *Rechazado:* lock global (serializa títulos sin relación) y fiarse de la comprobación de estado de la entidad (no es atómica). *Me haría cambiar:* varias instancias. Me apoyaría en la atomicidad por fila de Postgres: `UPDATE ejemplar SET estado='PRESTADO' WHERE id=? AND estado='DISPONIBLE'` con rowCount = 1 (o `FOR UPDATE SKIP LOCKED`), más `UNIQUE(ejemplar_id) WHERE devuelto_en IS NULL` como red de seguridad.

**B. Políticas tras una interfaz, con intercambio atómico.** `ProveedorPoliticas` sirve todos los números configurables (días, límites, 0,20, 10,00, 48h) desde un mapa inmutable, sin locks de lectura. Los préstamos ya concedidos conservan su vencimiento. *Rechazado:* constantes. *Me haría cambiar:* auditoría/histórico o valores por tenant (tabla versionada).

**C. Caducidad perezosa; la Reserva es la única fuente de verdad.** Sin scheduler: las reservas caducadas se procesan dentro del lock del título cuando alguien lo toca, y `Ejemplar` no guarda datos de la reserva. *Rechazado:* job en segundo plano y duplicar datos en el ejemplar (pueden contradecirse). *Me haría cambiar:* avisar al socio cuando su reserva caduca.

## 2. Fuera de alcance
Renovaciones (modelo y política conservan los límites); pago de multas; cancelar reservas y notificaciones; administración; API HTTP y punto de entrada (servicios cableados a mano); Postgres; **tests**.

## 3. Roto y entregado igualmente
- El límite de préstamos por socio abarca varios títulos y el lock por título no lo cubre: dos préstamos simultáneos de títulos distintos pueden superarlo. Arreglo: lock por socio antes que el del título, o `FOR UPDATE` sobre el socio.
- Las lecturas entre títulos (préstamos, multas) no están sincronizadas.
- Un socio bloqueado no puede pagar nunca (no hay flujo de pago).
- Un ejemplar sigue RESERVADO pasadas las 48h hasta que se toca su título.
- Los locks no se eliminan; `activosDelSocio` recorre todos los préstamos.
- Sin tests: la garantía de concurrencia está argumentada, no demostrada.

## 4. Los siguientes 30 minutos
`BorrowConcurrencyTest` con 2000+ rondas de 16 hilos y socios nuevos cada ronda; tests de devolución con cola y de las 23:58; lock por socio; después, renovaciones.

## 5. IA
**Delegué:** Usé Claude como guía desde el enunciado y le pasé un prompt para la estructura inicial, generación de la arquitectura, las clases de dominio y el apoyo en la redacción, síntesis y pulido de este propio NOTES.md.
**Aporte clave de la IA:** La propuesta de implementar el gestor de concurrencia mediante `ReentrantLock` por cada ID de título apoyándose en un `ConcurrentHashMap` y un patrón funcional (`Supplier<T>`) para asegurar el bloque `try-finally` de forma limpia y reutilizable.
**Ayuda de seguridad:** Le pasé las clases de lógica de negocio y las de concurrencia para que comprobase posibles fallos de hilos, condiciones de carrera o ineficiencias.