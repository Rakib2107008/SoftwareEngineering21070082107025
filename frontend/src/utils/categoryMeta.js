import {
  FaApple,
  FaBatteryHalf,
  FaHeadphones,
  FaHome,
  FaLaptop,
  FaMobileAlt,
  FaPlug,
  FaPuzzlePiece,
  FaTabletAlt,
  FaTag,
  FaTv,
} from "react-icons/fa";

export const categories = [
  "SMARTPHONE",
  "TABLET",
  "SMART_TV",
  "ADAPTER",
  "LAPTOP",
  "ACCESSORIES",
  "IPAD",
  "AIRPOD",
  "POWER_BANK",
  "HEADPHONES",
];

const categoryIconMap = {
  SMARTPHONE: FaMobileAlt,
  TABLET: FaTabletAlt,
  SMART_TV: FaTv,
  ADAPTER: FaPlug,
  LAPTOP: FaLaptop,
  ACCESSORIES: FaPuzzlePiece,
  IPAD: FaApple,
  AIRPOD: FaHeadphones,
  POWER_BANK: FaBatteryHalf,
  HEADPHONES: FaHeadphones,
};

export const HomeIcon = FaHome;

export const getCategoryIcon = (category) => categoryIconMap[category] || FaTag;

export const formatCategoryLabel = (category) => category.replace(/_/g, " ");
