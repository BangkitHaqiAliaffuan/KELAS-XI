export type LocationCategory = "District" | "Clinic" | "Ward" | "Facility" | "Restroom";

export interface HospitalLocation {
  id: string;
  name: string;
  building: string;
  category: LocationCategory;
  location: string;
  svgId?: string;
  svgIndex?: number;
}

export const hospitalLocations: HospitalLocation[] = [
  { id: "kec-1", name: "BALONGBENDO", building: "Sidoarjo Regency", category: "District", location: "West Region", svgIndex: 0 },
  { id: "kec-2", name: "BUDURAN", building: "Sidoarjo Regency", category: "District", location: "Central Region", svgIndex: 1 },
  { id: "kec-3", name: "CANDI", building: "Sidoarjo Regency", category: "District", location: "Central Region", svgIndex: 2 },
  { id: "kec-4", name: "GEDANGAN", building: "Sidoarjo Regency", category: "District", location: "North Region", svgIndex: 3 },
  { id: "kec-5", name: "JABON", building: "Sidoarjo Regency", category: "District", location: "South Region", svgIndex: 4 },
  { id: "kec-6", name: "KREMBUNG", building: "Sidoarjo Regency", category: "District", location: "South Region", svgIndex: 5 },
  { id: "kec-7", name: "KRIAN", building: "Sidoarjo Regency", category: "District", location: "West Region", svgIndex: 6 },
  { id: "kec-8", name: "PORONG", building: "Sidoarjo Regency", category: "District", location: "South Region", svgIndex: 7 },
  { id: "kec-9", name: "PRAMBON", building: "Sidoarjo Regency", category: "District", location: "West Region", svgIndex: 8 },
  { id: "kec-10", name: "SEDATI", building: "Sidoarjo Regency", category: "District", location: "North Region", svgIndex: 9 },
  { id: "kec-11", name: "SIDOARJO", building: "Sidoarjo Regency", category: "District", location: "Central Region", svgIndex: 10 },
  { id: "kec-12", name: "SUKODONO", building: "Sidoarjo Regency", category: "District", location: "Central Region", svgIndex: 11 },
  { id: "kec-13", name: "TAMAN", building: "Sidoarjo Regency", category: "District", location: "North Region", svgIndex: 12 },
  { id: "kec-14", name: "TANGGULANGIN", building: "Sidoarjo Regency", category: "District", location: "South Region", svgIndex: 13 },
  { id: "kec-15", name: "TARIK", building: "Sidoarjo Regency", category: "District", location: "West Region", svgIndex: 14 },
  { id: "kec-16", name: "TULANGAN", building: "Sidoarjo Regency", category: "District", location: "South Region", svgIndex: 15 },
  { id: "kec-17", name: "WARU", building: "Sidoarjo Regency", category: "District", location: "North Region", svgIndex: 16 },
  { id: "kec-18", name: "WONOAYU", building: "Sidoarjo Regency", category: "District", location: "West Region", svgIndex: 17 },
];

export const categories: { label: string; value: LocationCategory; icon: string }[] = [
  { label: "District", value: "District", icon: "🗺️" },
  { label: "Clinic", value: "Clinic", icon: "🏥" },
  { label: "Ward", value: "Ward", icon: "🛏️" },
  { label: "Facility", value: "Facility", icon: "🔧" },
  { label: "Restroom", value: "Restroom", icon: "🚻" },
];
