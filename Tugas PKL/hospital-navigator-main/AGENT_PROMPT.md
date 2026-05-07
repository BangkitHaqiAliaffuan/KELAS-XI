# Prompt for Sub-Agent: API Migration Task

## Task Overview
Migrate Hospital Navigator application from static file imports to API-based data fetching. The backend Express.js server already has complete data and endpoints ready. Your job is to create the frontend service layer and update components to use the API.

## Context Files to Read First
1. Read `MIGRATION_CONTEXT_FOR_AGENT.md` - Complete migration guide with all details
2. Read `MIGRATION_TO_API.md` - High-level migration plan
3. Read `server/src/data/hospitalRooms.complete.js` - Complete backend data
4. Read `server/src/data/qrAnchors.js` - QR anchors data
5. Read `src/data/hospitalRoomInfo.ts` - Current frontend data structure
6. Read `src/data/hospitalRouteGraph.ts` - Current QR anchor structure

## Your Mission

Execute the migration in 6 phases following the detailed guide in `MIGRATION_CONTEXT_FOR_AGENT.md`:

### Phase 1: Backend Preparation (30 min)
1. Replace `server/src/data/hospitalRooms.js` with content from `hospitalRooms.complete.js`
2. Delete `hospitalRooms.complete.js` after copying
3. Verify backend routes are working
4. Test backend endpoints

### Phase 2: Frontend API Service Layer (1 hour)
1. Create `src/services/api.ts` with axios setup and API methods
2. Create `src/hooks/useHospitalData.ts` with React Query hooks
3. Create `src/utils/apiHelpers.ts` with conversion helpers

### Phase 3: Update Components (2 hours)
Update these components to use API hooks:
1. `src/components/hospital/SearchBar.tsx`
2. `src/components/hospital/NavigationDialog.tsx`
3. `src/components/hospital/MapViewer.tsx`
4. `src/components/hospital/LocationInfoCard.tsx`

Add loading and error states to each component.

### Phase 4: Environment Configuration (15 min)
1. Create `.env` with `VITE_API_URL=http://localhost:3001/api/v1`
2. Create `.env.example`
3. Update `.gitignore` to include `.env`
4. Verify `server/.env` has correct CORS settings

### Phase 5: Testing (1 hour)
1. Start backend server: `cd server && npm run dev`
2. Test all backend endpoints with curl
3. Start frontend: `npm run dev`
4. Test all features manually:
   - Search functionality
   - Room selection
   - Navigation dialog
   - QR code scanning
   - Map display
   - Floor switching

### Phase 6: Cleanup (30 min)
1. Update imports to use types only where possible
2. Document any issues found
3. Create summary of changes

## Critical Requirements

### ✅ DO:
- Follow the exact code patterns in `MIGRATION_CONTEXT_FOR_AGENT.md`
- Add loading states to all components
- Add error handling with user-friendly messages
- Use React Query for caching (5 min stale time)
- Convert API arrays to objects using helper functions
- Test thoroughly before marking as complete
- Keep routing logic in frontend (don't migrate to API)

### ❌ DON'T:
- Delete `src/data/hospitalRoomInfo.ts` (keep for types and configs)
- Delete `src/data/hospitalRouteGraph.ts` (keep for routing functions)
- Change routing/pathfinding logic
- Remove type definitions
- Skip error handling
- Skip loading states
- Make breaking changes to component props

## Data Structures to Maintain

### Room Object
```typescript
{
  id: string;
  name: string;
  category: string;
  locationHint: string;
  description: string;
  floor: number; // -1, 0, 1, 2
}
```

### QR Anchor Object
```typescript
{
  qrId: string;
  roomId: string;
  svgX: number;
  svgY: number;
  label: string;
  floor: number;
  routeNodeId?: string;
}
```

## Success Criteria

Before marking as complete, verify:
- [ ] Backend serves all 80+ rooms
- [ ] Backend serves all 10+ QR anchors
- [ ] Frontend fetches data from API
- [ ] Search bar works with API data
- [ ] Navigation dialog works with API data
- [ ] Map viewer displays correctly
- [ ] QR code scanning works
- [ ] Floor switching works
- [ ] Loading states display
- [ ] Error states display
- [ ] No console errors
- [ ] All tests pass

## Testing Commands

### Backend
```bash
cd server
npm run dev

# Test in another terminal
curl http://localhost:3001/api/v1/rooms
curl http://localhost:3001/api/v1/qr-anchors
curl "http://localhost:3001/api/v1/rooms/search?q=IGD"
```

### Frontend
```bash
npm run dev
# Open http://localhost:5173
# Test all features manually
```

## Expected Output

At the end, provide:
1. Summary of all files created
2. Summary of all files modified
3. List of any issues encountered
4. Testing results
5. Recommendations for next steps

## Important Notes

- Backend data is already complete and synchronized with frontend
- API endpoints are already implemented and working
- Focus on frontend service layer and component updates
- Maintain backward compatibility with existing code
- Use helper functions to convert data formats
- Keep routing logic in frontend for performance

## Questions?

If you encounter issues:
1. Check `MIGRATION_CONTEXT_FOR_AGENT.md` for detailed solutions
2. Verify backend is running on port 3001
3. Check CORS settings in `server/.env`
4. Verify `.env` file exists in root with correct API URL
5. Check browser console for errors
6. Check network tab for API calls

## Start Here

1. Read `MIGRATION_CONTEXT_FOR_AGENT.md` completely
2. Start with Phase 1 (Backend Preparation)
3. Work through each phase sequentially
4. Test after each phase
5. Document any issues

Good luck! 🚀
