import { FaBroom, FaFilter } from "react-icons/fa";

function FilterBar({ title, onClear, children }) {
  return (
    <section className="filter-card">
      <div className="filter-head">
        <h4 className="btn-with-icon">
          <FaFilter aria-hidden="true" />
          <span>{title}</span>
        </h4>
        <button type="button" className="action-btn btn-with-icon" onClick={onClear}>
          <FaBroom aria-hidden="true" />
          <span>Clear Filters</span>
        </button>
      </div>
      <div className="filter-grid">{children}</div>
    </section>
  );
}

export default FilterBar;
