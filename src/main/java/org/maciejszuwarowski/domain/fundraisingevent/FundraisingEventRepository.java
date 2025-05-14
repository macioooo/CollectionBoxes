package org.maciejszuwarowski.domain.fundraisingevent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, String> {

}
