CREATE UNIQUE INDEX uq_open_repair_request_auto
    ON repair_requests(auto_id)
    WHERE status IN ('NEW', 'IN_PROGRESS');